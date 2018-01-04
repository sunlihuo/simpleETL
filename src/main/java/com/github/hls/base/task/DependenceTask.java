package com.github.hls.base.task;

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

    public void isNotKeepGoing(SimpleJobDO simpleJob){
        //未完成
        if (simpleJobServer.isParentWaiting(simpleJob)){
            log.info("simpleJob isParentWaiting true; simpleJob"+simpleJob);
            throw new RuntimeException();
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
