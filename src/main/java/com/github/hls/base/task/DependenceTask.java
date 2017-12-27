package com.github.hls.base.task;

import com.github.hls.domain.SimpleJobDO;
import com.github.hls.service.SimpleJobServer;
import lombok.extern.log4j.Log4j;

import javax.annotation.Resource;

/**
 * 血缘依赖
 */
@Log4j
public class DependenceTask {

    @Resource
    private SimpleJobServer simpleJobServer;

    public void handle(SimpleJobDO simpleJob){
        simpleJobServer.isParentSuccess(simpleJob);


    }
}
