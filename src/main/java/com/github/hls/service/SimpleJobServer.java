package com.github.hls.service;

import com.github.hls.domain.SimpleJobDO;
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
    private SimpleJobStatusMapper simpleJobStatusMapperl;

    public List<SimpleJobDO> queryJob(SimpleJobDO simpleJobDO){
        final Example example = new Example(SimpleJobDO.class);
        final Example.Criteria criteria = example.createCriteria();
        if (!isEmpty(simpleJobDO.getSimpleJobId())) {
            criteria.andEqualTo("simpleJobId", simpleJobDO.getSimpleJobId());
        }
        if (!isEmpty(simpleJobDO.getJobName())) {
            criteria.andEqualTo("jobName", simpleJobDO.getJobName());
        }
        example.orderBy("jobName");
        example.orderBy("executeOrder");
        List<SimpleJobDO> jobList = simpleJobMapper.selectByExample(example);
        return jobList;
    }
}
