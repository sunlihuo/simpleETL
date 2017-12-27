package com.github.hls.controller;

import com.github.hls.domain.SimpleJobDO;
import com.github.hls.domain.SimpleJobMonitorDO;
import com.github.hls.service.SimpleJobServer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class JobController {
    @Resource
    private SimpleJobServer simpleJobServer;


    @RequestMapping("/job")
    public List<SimpleJobDO> job(SimpleJobDO simpleJobDO){
        return simpleJobServer.queryJob(simpleJobDO);
    }


    @RequestMapping("/job2")
    public boolean job(SimpleJobMonitorDO simpleJobMonitorDO){
        return simpleJobServer.isParentSuccess(simpleJobMonitorDO);
    }
}
