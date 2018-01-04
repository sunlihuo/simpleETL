package com.github.hls.base.task;

import com.github.hls.base.exception.DependenceException;
import com.github.hls.domain.SimpleJobDO;
import com.github.hls.domain.SimpleJobMonitorDO;
import com.github.hls.service.RocketMQProducerServer;
import com.github.hls.service.SimpleJobServer;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 血缘依赖
 */
@Component
@Log4j
public class DependenceTask {

    @Resource
    private SimpleJobServer simpleJobServer;
    @Resource
    private RocketMQProducerServer rocketMQProducerServer;

    public void isNotKeepGoing(SimpleJobDO simpleJob) throws DependenceException {
        //未完成
        if (simpleJobServer.isParentWaiting(simpleJob)){
            log.info("父任务没有完成,此任务不执行 simpleJob parent job is waiting; jobName=" + simpleJob.getJobName());
            throw new DependenceException("父任务没有完成");
        } else {
            return;
        }
    }

    public void handleWaitingSimpleJob(SimpleJobDO simpleJob){
        //依赖子任务触发
        final List<SimpleJobMonitorDO> simpleJobMonitorDOS = simpleJobServer.queryWaitingSimpleJob(simpleJob);
        //发mq
        for (SimpleJobMonitorDO jobMonitor : simpleJobMonitorDOS) {
            rocketMQProducerServer.sendJobName(jobMonitor.getJobName());
        }
    }
}
