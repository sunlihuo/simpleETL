package com.github.hls.service;

import com.github.hls.domain.SimpleJobDO;
import com.github.hls.domain.SimpleJobStatusDO;
import com.github.hls.mapper.SimpleJobMapper;
import com.github.hls.mapper.SimpleJobStatusMapper;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

import static org.springframework.util.StringUtils.isEmpty;

@Service
public class SimpleJobServer {
    @Resource
    private SimpleJobMapper simpleJobMapper;
    @Resource
    private SimpleJobStatusMapper simpleJobStatusMapper;

    public List<SimpleJobDO> queryJob(SimpleJobDO simpleJobDO){
        final Example example = new Example(SimpleJobDO.class);
        final Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("simpleJobId", simpleJobDO.getSimpleJobId());
        criteria.andEqualTo("jobName", simpleJobDO.getJobName());
        example.orderBy("jobName");
        example.orderBy("executeOrder");
        List<SimpleJobDO> jobList = simpleJobMapper.selectByExample(example);
        return jobList;
    }

    public List<SimpleJobStatusDO> queryParentJobStatus(SimpleJobStatusDO simpleJobStatusDO){
        final Example example = new Example(SimpleJobStatusDO.class);
        final Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("jobName", simpleJobStatusDO.getJobName());
        criteria.andCondition("DATE_FORMAT(stampDate,'%Y-%m-%d')=DATE_FORMAT(CURDATE(),'%Y-%m-%d')");
        return simpleJobStatusMapper.selectByExample(example);
    }

    public boolean isParentSuccess(SimpleJobStatusDO simpleJobStatusDO){
        List<SimpleJobStatusDO> simpleJobStatusDOS = queryParentJobStatus(simpleJobStatusDO);
        if (null == simpleJobStatusDOS || simpleJobStatusDOS.isEmpty()){
            return true;
        }

        for (SimpleJobStatusDO jobStatus : simpleJobStatusDOS) {
            if ("success".equalsIgnoreCase(jobStatus.getIsSuccess())){
                return true;
            }
        }

        return false;
    }
}
