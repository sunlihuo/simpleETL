package com.github.hls.etl.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.hls.etl.base.enums.HandleTypeEnum;
import com.github.hls.etl.domain.SimpleETLDO;
import com.github.hls.etl.mapper.SimpleETLMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class SimpleETLService extends ServiceImpl<SimpleETLMapper, SimpleETLDO> {


    /**
     * 查询需要执行的任务
     * @param job
     * @param admin
     * @return
     */
    public List<SimpleETLDO> queryRunningJob(SimpleETLDO job, String admin){
        LambdaQueryWrapper<SimpleETLDO> query = Wrappers.lambdaQuery();
        query.eq(job.getSimpleJobId()!= null, SimpleETLDO::getSimpleJobId, job.getSimpleJobId());
        query.eq(job.getJobName()!= null, SimpleETLDO::getJobName, job.getJobName());
        if (admin == null) {
            query.ne(SimpleETLDO::getStatus, 0);
        }
        query.ne(SimpleETLDO::getHandleType, HandleTypeEnum.全局_参数.getCode());
        query.orderByAsc(SimpleETLDO::getJobName, SimpleETLDO::getExecuteOrder, SimpleETLDO::getGmtCreate);
        List<SimpleETLDO> jobList = this.list(query);
        return jobList;
    }

    /**
     * 查询全局参数任务
     * @param job
     * @return
     */
    public List<SimpleETLDO> querySysValueRunningJob(SimpleETLDO job){
        LambdaQueryWrapper<SimpleETLDO> query = Wrappers.lambdaQuery();
        query.eq(job.getSimpleJobId()!= null, SimpleETLDO::getSimpleJobId, job.getSimpleJobId());
        query.eq(job.getJobName()!= null, SimpleETLDO::getJobName, job.getJobName());
        query.ne(SimpleETLDO::getStatus, 0);
        query.eq(SimpleETLDO::getHandleType, HandleTypeEnum.全局_参数.getCode());
        query.orderByAsc(SimpleETLDO::getJobName, SimpleETLDO::getExecuteOrder, SimpleETLDO::getGmtCreate);
        List<SimpleETLDO> jobList = this.list(query);
        return jobList;
    }

    /**
     * 减一次使用
     * -1为永远执行
     * 0不执行
     * 2表示可执行2次
     * @param simpleJob
     */
    public void subtractStatus(SimpleETLDO simpleJob){
        if (simpleJob.getStatus().intValue() >= 1) {
            Integer status = simpleJob.getStatus();
            status--;
            simpleJob.setStatus(status);
        }
        simpleJob.setGmtRunning(LocalDateTime.now());
        this.updateById(simpleJob);
    }

}
