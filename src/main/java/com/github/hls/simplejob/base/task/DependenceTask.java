package com.github.hls.simplejob.base.task;//package com.github.hls.simplejob.base.task;
//
//import com.github.hls.simplejob.base.exception.DependenceException;
//import com.github.hls.simplejob.domain.SimpleJobEntity;
//import com.github.hls.simplejob.domain.SimpleJobMonitorDO;
//import com.github.hls.simplejob.service.SimpleJobService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import java.util.List;
//
///**
// * 血缘依赖
// */
//@Component
//@Slf4j
//public class DependenceTask {
//
//    @Resource
//    private SimpleJobService simpleJobService;
//    @Resource
//    private RocketMQProducerServer rocketMQProducerServer;
//
//    public void isNotKeepGoing(SimpleJobEntity simpleJob) throws DependenceException {
//        //未完成
//        if (simpleJobService.isParentWaiting(simpleJob)){
//            log.info("父任务没有完成,此任务不执行 simpleJob parent job is waiting; jobName=" + simpleJob.getJobName());
//            throw new DependenceException("父任务没有完成");
//        }
//    }
//
//    public void handleWaitingSimpleJob(SimpleJobEntity simpleJob){
//        //依赖子任务触发
//        final List<SimpleJobMonitorDO> simpleJobMonitorDOS = simpleJobService.queryWaitingSimpleJob(simpleJob);
//        //发mq
//        for (SimpleJobMonitorDO jobMonitor : simpleJobMonitorDOS) {
//            rocketMQProducerServer.sendJobName(jobMonitor.getJobName());
//        }
//    }
//}
