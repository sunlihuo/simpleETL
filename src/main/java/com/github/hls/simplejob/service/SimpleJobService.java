package com.github.hls.simplejob.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.hls.simplejob.domain.SimpleJobEntity;
import com.github.hls.simplejob.mapper.SimpleJobMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service("simpleJobService")
public class SimpleJobService extends ServiceImpl<SimpleJobMapper, SimpleJobEntity> {


    public List<SimpleJobEntity> queryRunningJob(SimpleJobEntity simpleJobEntity, String admin){
        LambdaQueryWrapper<SimpleJobEntity> query = Wrappers.lambdaQuery();
        query.eq(simpleJobEntity.getSimpleJobId()!= null, SimpleJobEntity::getSimpleJobId, simpleJobEntity.getSimpleJobId());
        query.eq(simpleJobEntity.getJobName()!= null, SimpleJobEntity::getJobName, simpleJobEntity.getJobName());
        if (admin == null) {
            query.ne(SimpleJobEntity::getStatus, 0);
        }
        query.orderByAsc(SimpleJobEntity::getJobName, SimpleJobEntity::getExecuteOrder, SimpleJobEntity::getGmtCreate);
        List<SimpleJobEntity> jobList = this.list(query);
        return jobList;
    }

    public void subtractStatus(SimpleJobEntity simpleJob){
        if (simpleJob.getStatus().longValue() >= 1L) {
            Long status = simpleJob.getStatus();
            status--;
            simpleJob.setStatus(status);
            this.updateById(simpleJob);
        }
    }

}