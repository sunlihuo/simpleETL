package com.github.hls.simplejob.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.hls.simplejob.base.enums.HandleTypeEnum;
import com.github.hls.simplejob.domain.SimpleJobDO;
import com.github.hls.simplejob.mapper.SimpleJobMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class SimpleJobService extends ServiceImpl<SimpleJobMapper, SimpleJobDO> {


    /**
     * 查询需要执行的任务
     * @param job
     * @param admin
     * @return
     */
    public List<SimpleJobDO> queryRunningJob(SimpleJobDO job, String admin){
        LambdaQueryWrapper<SimpleJobDO> query = Wrappers.lambdaQuery();
        query.eq(job.getSimpleJobId()!= null, SimpleJobDO::getSimpleJobId, job.getSimpleJobId());
        query.eq(job.getJobName()!= null, SimpleJobDO::getJobName, job.getJobName());
        if (admin == null) {
            query.ne(SimpleJobDO::getStatus, 0);
        }
        query.ne(SimpleJobDO::getHandleType, HandleTypeEnum.全局_参数.getCode());
        query.orderByAsc(SimpleJobDO::getJobName, SimpleJobDO::getExecuteOrder, SimpleJobDO::getGmtCreate);
        List<SimpleJobDO> jobList = this.list(query);
        return jobList;
    }

    /**
     * 查询全局参数任务
     * @param job
     * @return
     */
    public List<SimpleJobDO> querySysValueRunningJob(SimpleJobDO job){
        LambdaQueryWrapper<SimpleJobDO> query = Wrappers.lambdaQuery();
        query.eq(job.getSimpleJobId()!= null, SimpleJobDO::getSimpleJobId, job.getSimpleJobId());
        query.eq(job.getJobName()!= null, SimpleJobDO::getJobName, job.getJobName());
        query.ne(SimpleJobDO::getStatus, 0);
        query.eq(SimpleJobDO::getHandleType, HandleTypeEnum.全局_参数.getCode());
        query.orderByAsc(SimpleJobDO::getJobName, SimpleJobDO::getExecuteOrder, SimpleJobDO::getGmtCreate);
        List<SimpleJobDO> jobList = this.list(query);
        return jobList;
    }

    /**
     * 减一次使用
     * -1为永远执行
     * 0不执行
     * 2表示可执行2次
     * @param simpleJob
     */
    public void subtractStatus(SimpleJobDO simpleJob){
        if (simpleJob.getStatus().intValue() >= 1) {
            Integer status = simpleJob.getStatus();
            status--;
            simpleJob.setStatus(status);
        }
        simpleJob.setGmtRunning(LocalDateTime.now());
        this.updateById(simpleJob);
    }

}
