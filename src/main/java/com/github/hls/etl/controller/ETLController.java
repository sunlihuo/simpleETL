package com.github.hls.etl.controller;

import com.github.hls.etl.base.simplejob.SectionValueStrategy;
import com.github.hls.etl.base.task.SimpleETLTask;
import com.github.hls.etl.domain.SimpleETLDO;
import com.github.hls.etl.domain.SimpleETLRO;
import com.github.hls.etl.service.SimpleETLService;
import com.github.hls.etl.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.sql.DataSource;

@RequestMapping("/simplejob")
@RestController
public class ETLController {
    @Resource
    private SimpleETLTask simpleJobTask;
    @Resource
    private SimpleETLService simpleJobService;
    @Autowired
    private SectionValueStrategy sectionValueStrategy;
    @Resource
    private DataSource datacenterDataSource;

    @RequestMapping("/update")
    public String update(SimpleETLDO simpleJobDO, String password) {
        if (!"fewf14#653#g".equals(password)) {
            return "error";
        }
        simpleJobService.updateById(simpleJobDO);
        return "success";
    }

    @RequestMapping("/job")
    public String job(SimpleETLRO simpleJobRO, String password) {
        SimpleETLDO simpleJobEntity = BeanUtils.copyProperties(simpleJobRO, SimpleETLDO.class);
        if (!"fewf14#653#g".equals(password)) {
            return "password error";
        }
        sectionValueStrategy.doHandle(simpleJobEntity, datacenterDataSource);
        simpleJobTask.handleHttp(simpleJobEntity, "admin");

        /*new Thread(() -> simpleJobTask.handleHttp(simpleJobDO));*/
        return "success";
    }


}
