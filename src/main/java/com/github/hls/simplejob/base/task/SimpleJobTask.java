package com.github.hls.simplejob.base.task;

import com.github.hls.simplejob.base.disruptor.Disruptor;
import com.github.hls.simplejob.base.disruptor.Producer;
import com.github.hls.simplejob.base.exception.DependenceException;
import com.github.hls.simplejob.base.simplejob.base.SimpleJobStrategy;
import com.github.hls.simplejob.domain.SimpleJobEntity;
import com.github.hls.simplejob.base.enums.HandleTypeEnum;
import com.github.hls.simplejob.service.SimpleJobService;
import com.github.hls.simplejob.utils.DateUtils;
import com.github.hls.simplejob.utils.SimpleJobUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.github.hls.simplejob.utils.SimpleJobUtils.transList2Map;


@Service
@Slf4j
public class SimpleJobTask {

    @Resource
    private SimpleJobStrategy sectionValueStrategy;
    @Resource
    private SimpleJobStrategy autoPageStrategy;
    @Resource
    private SimpleJobService simpleJobService;
    @Resource
    private Disruptor disruptor;

    /**
     * 任务执行
     * @param simpleJobDO
     * @param admin 不为空时可以执行任何job
     * @return
     */
    public boolean handleHttp(SimpleJobEntity simpleJobDO, String admin) {
        final List<SimpleJobEntity> simpleJobS = simpleJobService.queryRunningJob(simpleJobDO, admin);
        if (simpleJobS == null || simpleJobS.size() == 0) {
            log.error("simplejob is null");
            return false;
        }

        handleJob(simpleJobS);
        return true;
    }

    public void handleJob(List<SimpleJobEntity> simpleJobList) {
        Producer producer = disruptor.getProducer();

        try {
            long countCurrent = System.currentTimeMillis();
            int i = 0;
            final Map<String, List<SimpleJobEntity>> simpleJobMap = transList2Map(simpleJobList);
            log.info("开始执行任务{}", simpleJobMap.values().size());

            final Iterator<List<SimpleJobEntity>> iterator = simpleJobMap.values().iterator();
            while (iterator.hasNext()) {
                List<SimpleJobEntity> jobList = iterator.next();

                for (SimpleJobEntity simpleJob : jobList) {
                    Long current = System.currentTimeMillis();
                    log.info("开始第{}个任务,jobId:{},jobName:{},sourceType:{}", ++i, simpleJob.getSimpleJobId(), simpleJob.getJobName(), simpleJob.getHandleType());
                    try {
                        if (HandleTypeEnum.分段_参数.getCode().equals(simpleJob.getHandleType())) {
                            sectionValueStrategy.setProducer(producer);
                            sectionValueStrategy.handle(simpleJob);
                        } else{
                            autoPageStrategy.setProducer(producer);
                            autoPageStrategy.handle(simpleJob);
                        }

                    } catch (Exception e) {
                        if (e instanceof DependenceException) {
                            break;
                        }

                        log.error("SimpleJobTask error", e);
                        if (!"Y".equalsIgnoreCase(simpleJob.getErrorGoOn())) {
                            break;
                        }
                    }

                    log.info("结束第{}个任务, jobId:{},jobName:{},耗时:{}", i, simpleJob.getSimpleJobId(), simpleJob.getJobName(), DateUtils.dateDiff(current, System.currentTimeMillis()));
                    simpleJobService.subtractStatus(simpleJob);
                }
                SimpleJobUtils.sectionList.clear();
            }

            log.info("结束任务, 耗时:{}", DateUtils.dateDiff(countCurrent, System.currentTimeMillis()));
        } catch (Exception e) {
            log.error("SimpleJobTask error", e);
        } finally {
            disruptor.drainAndHalt();
        }
    }
}
