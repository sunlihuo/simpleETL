package com.github.hls.etl.controller;

import com.github.hls.etl.base.etl.SectionValueStrategy;
import com.github.hls.etl.base.task.SimpleETLTask;
import com.github.hls.etl.domain.SimpleETLDO;
import com.github.hls.etl.domain.SimpleETLRO;
import com.github.hls.etl.service.SimpleETLService;
import com.github.hls.etl.utils.BeanUtils;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.sql.DataSource;

@RequestMapping("/etl")
@RestController
public class ETLController {
    @Resource
    private SimpleETLTask etlTask;
    @Resource
    private SimpleETLService etlService;
    @Autowired
    private SectionValueStrategy sectionValueStrategy;
    @Resource
    private DataSource datacenterDataSource;

    @RequestMapping("/update")
    public String update(SimpleETLDO etlDO, String password) {
        if (!"fewf14#653#g".equals(password)) {
            return "error";
        }
        etlService.updateById(etlDO);
        return "success";
    }

    @RequestMapping("/run")
    public String etl(SimpleETLRO etlRO, String password) {
        SimpleETLDO etlEntity = BeanUtils.copyProperties(etlRO, SimpleETLDO.class);
        if (!"fewf14#653#g".equals(password)) {
            return "password error";
        }
        sectionValueStrategy.doHandle(etlEntity, datacenterDataSource);
        etlTask.handleHttp(etlEntity, "admin");

        /*new Thread(() -> etlTask.handleHttp(etlDO));*/
        return "success";
    }


    @SneakyThrows
    @Scheduled(cron = "0/5 * * * * ? ")
    public void daily() {
        SimpleETLRO etlRO = new SimpleETLRO();
        SimpleETLDO etlEntity = BeanUtils.copyProperties(etlRO, SimpleETLDO.class);
        etlEntity.setName("numkk5");
        sectionValueStrategy.doHandle(etlEntity, datacenterDataSource);
        etlTask.handleHttp(etlEntity, "admin");
    }
}
