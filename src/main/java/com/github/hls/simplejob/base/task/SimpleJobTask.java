package com.github.hls.simplejob.base.task;

import com.github.hls.simplejob.base.disruptor.Disruptor;
import com.github.hls.simplejob.base.disruptor.Producer;
import com.github.hls.simplejob.base.enums.SimpleJobEnum;
import com.github.hls.simplejob.base.exception.DependenceException;
import com.github.hls.simplejob.base.simplejob.base.SimpleJobStrategy;
import com.github.hls.simplejob.domain.SimpleJobEntity;
import com.github.hls.simplejob.service.SimpleJobService;
import com.github.hls.simplejob.utils.DateUtils;
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
    SimpleJobStrategy autoStrategy;
    @Resource
    SimpleJobStrategy sectionValueStrategy;
    @Resource
    SimpleJobStrategy normalStrategy;
    ;

    @Resource
    private SimpleJobService simpleJobService;
    @Resource
    private Disruptor disruptor;
//    @Resource
//    private DependenceTask dependenceTask;

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
                boolean isSuccess = true;
                List<SimpleJobEntity> jobList = iterator.next();

                for (SimpleJobEntity simpleJob : jobList) {
                    Long current = System.currentTimeMillis();
                    log.info("开始第{}个任务,jobId:{},jobName:{},sourceType:{}", ++i, simpleJob.getSimpleJobId(), simpleJob.getJobName(), simpleJob.getSourceType());
                    try {
                        //dependenceTask.isNotKeepGoing(simpleJob);

                        String beanName = SimpleJobEnum.SOURCE_TYPE.valueOf(simpleJob.getSourceType()).getBeanName();
//                        SimpleJobStrategy simpleJobStrategy = SpringUtil.getBean(beanName);
                        if ("sectionValueStrategy".equalsIgnoreCase(beanName)) {
                            sectionValueStrategy.setProducer(producer);
                            sectionValueStrategy.handle(simpleJob);
                        } else if ("autoStrategy".equalsIgnoreCase(beanName)) {
                            autoStrategy.setProducer(producer);
                            autoStrategy.handle(simpleJob);
                        } else if ("normalStrategy".equalsIgnoreCase(beanName)) {
                            normalStrategy.setProducer(producer);
                            normalStrategy.handle(simpleJob);
                        }

                    } catch (Exception e) {
                        isSuccess = false;
                        if (e instanceof DependenceException) {
                            //simpleJobService.insertJobMonitor(jobList.get(0), "waiting");
                            break;
                        }

                        log.error("SimpleJobTask error", e);
                        //simpleJobService.insertJobMonitor(jobList.get(0), "waiting");
                        if (!"Y".equalsIgnoreCase(simpleJob.getErrorGoOn())) {
                            break;
                        }
                    }

                    log.info("结束第{}个任务, jobId:{},jobName:{},耗时:{}", i, simpleJob.getSimpleJobId(), simpleJob.getJobName(), DateUtils.dateDiff(current, System.currentTimeMillis()));
                    simpleJobService.subtractStatus(simpleJob);
                }

                if (isSuccess) {
                    //一组任务完成
                    //simpleJobService.insertJobMonitor(jobList.get(0), "success");
                    //每组完成后，依赖子任务触发
                    //dependenceTask.handleWaitingSimpleJob(jobList.get(0));
                }

            }

            log.info("结束任务, 耗时:{}", DateUtils.dateDiff(countCurrent, System.currentTimeMillis()));
        } catch (Exception e) {
            log.error("SimpleJobTask error", e);
        } finally {
            disruptor.drainAndHalt();
        }

    }


}