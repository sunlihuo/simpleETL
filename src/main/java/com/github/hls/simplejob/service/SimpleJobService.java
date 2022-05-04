package com.github.hls.simplejob.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.hls.simplejob.base.enums.HandleTypeEnum;
import com.github.hls.simplejob.domain.SimpleJobEntity;
import com.github.hls.simplejob.mapper.SimpleJobMapper;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service("simpleJobService")
public class SimpleJobService extends ServiceImpl<SimpleJobMapper, SimpleJobEntity> {


    /**
     * 查询需要执行的任务
     * @param job
     * @param admin
     * @return
     */
    public List<SimpleJobEntity> queryRunningJob(SimpleJobEntity job, String admin){
        LambdaQueryWrapper<SimpleJobEntity> query = Wrappers.lambdaQuery();
        query.eq(job.getSimpleJobId()!= null, SimpleJobEntity::getSimpleJobId, job.getSimpleJobId());
        query.eq(job.getJobName()!= null, SimpleJobEntity::getJobName, job.getJobName());
        if (admin == null) {
            query.ne(SimpleJobEntity::getStatus, 0);
        }
        query.ne(SimpleJobEntity::getHandleType, HandleTypeEnum.全局_参数.getCode());
        query.orderByAsc(SimpleJobEntity::getJobName, SimpleJobEntity::getExecuteOrder, SimpleJobEntity::getGmtCreate);
        List<SimpleJobEntity> jobList = this.list(query);
        return jobList;
    }

    /**
     * 查询全局参数任务
     * @param job
     * @return
     */
    public List<SimpleJobEntity> querySysValueRunningJob(SimpleJobEntity job){
        LambdaQueryWrapper<SimpleJobEntity> query = Wrappers.lambdaQuery();
        query.eq(job.getSimpleJobId()!= null, SimpleJobEntity::getSimpleJobId, job.getSimpleJobId());
        query.eq(job.getJobName()!= null, SimpleJobEntity::getJobName, job.getJobName());
        query.ne(SimpleJobEntity::getStatus, 0);
        query.eq(SimpleJobEntity::getHandleType, HandleTypeEnum.全局_参数.getCode());
        query.orderByAsc(SimpleJobEntity::getJobName, SimpleJobEntity::getExecuteOrder, SimpleJobEntity::getGmtCreate);
        List<SimpleJobEntity> jobList = this.list(query);
        return jobList;
    }

    /**
     * 减一次使用
     * -1为永远执行
     * 0不执行
     * 2表示可执行2次
     * @param simpleJob
     */
    public void subtractStatus(SimpleJobEntity simpleJob){
        if (simpleJob.getStatus().intValue() >= 1) {
            Integer status = simpleJob.getStatus();
            status--;
            simpleJob.setStatus(status);
        }
        simpleJob.setGmtRunning(LocalDateTime.now());
        this.updateById(simpleJob);
    }

}
