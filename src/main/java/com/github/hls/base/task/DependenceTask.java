package com.github.hls.base.task;

import com.github.hls.domain.SimpleJobDO;
import com.github.hls.service.SimpleJobServer;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 血缘依赖
 */
@Component
@Log4j
public class DependenceTask {

    @Resource
    private SimpleJobServer simpleJobServer;

    public boolean isNotKeepGoing(SimpleJobDO simpleJob){
        //未完成
        if (simpleJobServer.isParentWaiting(simpleJob)){
            log.info("simpleJob isParentWaiting true; simpleJob"+simpleJob);
            simpleJobServer.insertJobMonitor(simpleJob, "waiting");
            return true;
        } else {
        //已经完成
            return false;
        }

    }
}
