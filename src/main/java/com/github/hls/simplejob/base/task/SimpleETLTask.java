package com.github.hls.simplejob.base.task;

import com.github.hls.simplejob.base.disruptor.Disruptor;
import com.github.hls.simplejob.base.disruptor.Producer;
import com.github.hls.simplejob.base.exception.DependenceException;
import com.github.hls.simplejob.base.simplejob.base.SimpleETLStrategy;
import com.github.hls.simplejob.domain.SimpleETLDO;
import com.github.hls.simplejob.base.enums.HandleTypeEnum;
import com.github.hls.simplejob.service.SimpleETLService;
import com.github.hls.simplejob.utils.DateUtils;
import com.github.hls.simplejob.utils.SimpleDBUtils;
import com.github.hls.simplejob.utils.SimpleETLUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.github.hls.simplejob.utils.SimpleETLUtils.transList2Map;


@Service
@Slf4j
public class SimpleETLTask {

    @Resource
    private SimpleETLStrategy sectionValueStrategy;
    @Resource
    private SimpleETLStrategy autoPageStrategy;
    @Resource
    private SimpleETLService simpleJobService;
    @Resource
    private Disruptor disruptor;
    @Resource
    private DataSource datacenterDataSource;

    /**
     * 任务执行
     *
     * @param job
     * @param admin 不为空时可以执行任何job
     * @return
     */
    public boolean handleHttp(SimpleETLDO job, String admin) {
        List<SimpleETLDO> sysValueRunningJobList = simpleJobService.querySysValueRunningJob(job);
        if (!CollectionUtils.isEmpty(sysValueRunningJobList)) {
            handleSysValue(sysValueRunningJobList);
        }

        final List<SimpleETLDO> jobList = simpleJobService.queryRunningJob(job, admin);
        if (CollectionUtils.isEmpty(jobList)) {
            log.error("simplejob is null");
            return false;
        }

        handleJob(jobList);
        return true;
    }

    /**
     * 全局参数
     *
     * @param jobList
     */
    public void handleSysValue(List<SimpleETLDO> jobList) {
        if (CollectionUtils.isEmpty(jobList)) {
            return;
        }

        SimpleETLUtils.clearSysParam();
        jobList.stream().forEach(m -> {
            List<Map<String, Object>> maps = SimpleDBUtils.queryListMap(m.getSelectSql(), datacenterDataSource);
            maps.stream().forEach(map -> {
                map.keySet().stream().forEach(key -> {
                    SimpleETLUtils.putSysParam(key, map.get(key) == null ? "" : String.valueOf(map.get(key)));
                });
            });
        });
    }

    public void handleJob(List<SimpleETLDO> simpleJobList) {
        Producer producer = disruptor.getProducer();

        try {
            long countCurrent = System.currentTimeMillis();
            int i = 0;
            final Map<String, List<SimpleETLDO>> simpleJobMap = transList2Map(simpleJobList);
            log.info("开始执行任务{}", simpleJobMap.values().size());

            final Iterator<List<SimpleETLDO>> iterator = simpleJobMap.values().iterator();
            while (iterator.hasNext()) {
                List<SimpleETLDO> jobList = iterator.next();

                for (SimpleETLDO simpleJob : jobList) {
                    Long current = System.currentTimeMillis();
                    log.info("开始第{}个任务,jobId:{},jobName:{},sourceType:{}", ++i, simpleJob.getSimpleJobId(), simpleJob.getJobName(), simpleJob.getHandleType());
                    try {
                        if (HandleTypeEnum.分段_参数.getCode().equals(simpleJob.getHandleType())) {
                            sectionValueStrategy.setProducer(producer);
                            sectionValueStrategy.handle(simpleJob);
                        } else {
                            autoPageStrategy.setProducer(producer);
                            autoPageStrategy.handle(simpleJob);
                        }

                    } catch (Exception e) {
                        if (e instanceof DependenceException) {
                            break;
                        }

                        log.error("SimpleJobTask error", e);
                        if (simpleJob.getErrorGoOn() == 1) {
                            break;
                        }
                    }

                    log.info("结束第{}个任务, jobId:{},jobName:{},耗时:{}", i, simpleJob.getSimpleJobId(), simpleJob.getJobName(), DateUtils.dateDiff(current, System.currentTimeMillis()));
                    simpleJobService.subtractStatus(simpleJob);
                }
                SimpleETLUtils.sectionValueList.clear();
            }

            log.info("结束任务, 耗时:{}", DateUtils.dateDiff(countCurrent, System.currentTimeMillis()));
        } catch (Exception e) {
            log.error("SimpleJobTask error", e);
        } finally {
            disruptor.drainAndHalt();
        }
    }
}
