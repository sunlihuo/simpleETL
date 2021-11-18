package com.github.hls.simplejob.controller;

import com.github.hls.simplejob.base.task.SimpleJobTask;
import com.github.hls.simplejob.domain.SimpleJobEntity;
import com.github.hls.simplejob.service.SimpleJobService;
import com.github.hls.simplejob.utils.SimpleJobUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RequestMapping("/simplejob")
@RestController
public class JobController {
    @Resource
    private SimpleJobTask simpleJobTask;
    @Resource
    private SimpleJobService simpleJobService;

//    @RequestMapping("/queryList")
//    public APIResult<List<SimpleJobEntity>> queryList(){
//        SimpleJobEntity simpleJobEntity = new SimpleJobEntity();
//        List<SimpleJobEntity> list = simpleJobService.queryRunningJob(simpleJobEntity, null);
//        return APIResult.success(list);
//    }

    @RequestMapping("/update")
    public String update(SimpleJobEntity simpleJobDO, String password){
        if (!"453JGHg#dl5fe".equals(password)) {
            return "error";
        }
        simpleJobService.updateById(simpleJobDO);
        return "success";
    }

    @RequestMapping("/job")
    public String job(SimpleJobEntity simpleJobDO, String password){
        if (!"453JGHg#dl5fe".equals(password)) {
            return "password error";
        }

        SimpleJobUtils.sysParam.put("INTERVAL", simpleJobDO.getDescription());
        simpleJobTask.handleHttp(simpleJobDO, "admin");
        SimpleJobUtils.sysParam.clear();
        /*new Thread(() -> simpleJobTask.handleHttp(simpleJobDO));*/
        return "success";
    }



}
