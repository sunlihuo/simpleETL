package com.github.hls.simplejob.controller;

import com.github.hls.simplejob.base.simplejob.SectionValueStrategy;
import com.github.hls.simplejob.base.task.SimpleJobTask;
import com.github.hls.simplejob.domain.SimpleJobDO;
import com.github.hls.simplejob.domain.SimpleJobRO;
import com.github.hls.simplejob.service.SimpleJobService;
import com.github.hls.simplejob.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.sql.DataSource;

@RequestMapping("/simplejob")
@RestController
public class JobController {
    @Resource
    private SimpleJobTask simpleJobTask;
    @Resource
    private SimpleJobService simpleJobService;
    @Autowired
    private SectionValueStrategy sectionValueStrategy;
    @Resource
    private DataSource datacenterDataSource;

    @RequestMapping("/update")
    public String update(SimpleJobDO simpleJobDO, String password) {
        if (!"fewf14#653#g".equals(password)) {
            return "error";
        }
        simpleJobService.updateById(simpleJobDO);
        return "success";
    }

    @RequestMapping("/job")
    public String job(SimpleJobRO simpleJobRO, String password) {
        SimpleJobDO simpleJobEntity = BeanUtils.copyProperties(simpleJobRO, SimpleJobDO.class);
        if (!"fewf14#653#g".equals(password)) {
            return "password error";
        }
        sectionValueStrategy.doHandle(simpleJobEntity, datacenterDataSource);
        simpleJobTask.handleHttp(simpleJobEntity, "admin");

        /*new Thread(() -> simpleJobTask.handleHttp(simpleJobDO));*/
        return "success";
    }


}
