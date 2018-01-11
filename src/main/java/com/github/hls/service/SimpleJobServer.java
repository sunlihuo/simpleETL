package com.github.hls.service;

import com.github.hls.base.enums.SimpleJobEnum;
import com.github.hls.domain.BaseQueryInfo;
import com.github.hls.domain.SimpleJobDO;
import com.github.hls.domain.SimpleJobMonitorDO;
import com.github.hls.mapper.SimpleJobMapper;
import com.github.hls.mapper.SimpleJobMonitorMapper;
import com.github.pagehelper.Page;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class SimpleJobServer {
    @Resource
    private SimpleJobMapper simpleJobMapper;
    @Resource
    private SimpleJobMonitorMapper simpleJobMonitorMapper;

    public void update(SimpleJobDO simpleJobDO){
        simpleJobMapper.updateByPrimaryKeySelective(simpleJobDO);
    }

    public void update(SimpleJobMonitorDO simpleJobMonitorDO){
        simpleJobMonitorMapper.updateByPrimaryKeySelective(simpleJobMonitorDO);
    }

    public void insert(SimpleJobDO simpleJobDO){
        simpleJobMapper.insertSelective(simpleJobDO);
    }

    public void insert(SimpleJobMonitorDO simpleJobMonitorDO){
        simpleJobMonitorMapper.updateByPrimaryKeySelective(simpleJobMonitorDO);
    }

    public Page<SimpleJobMonitorDO> queryMonitorList(SimpleJobDO simpleJobDO) {
        final Example example = new Example(SimpleJobMonitorDO.class);
        final Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("jobName", simpleJobDO.getJobName());
        criteria.andEqualTo("status", simpleJobDO.getStatus());
        example.orderBy("stampDate");
        Page<SimpleJobMonitorDO> jobList = (Page<SimpleJobMonitorDO>) simpleJobMonitorMapper.selectByExample(example);
        return jobList;
    }

    public Page<SimpleJobDO> queryJob(SimpleJobDO simpleJobDO){
        final Example example = new Example(SimpleJobDO.class);
        final Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("simpleJobId", simpleJobDO.getSimpleJobId());
        criteria.andEqualTo("jobName", simpleJobDO.getJobName());
        criteria.andEqualTo("status", simpleJobDO.getStatus());
        example.orderBy("jobName");
        example.orderBy("executeOrder");
        example.orderBy("stampDate");
        Page<SimpleJobDO> jobList = (Page<SimpleJobDO>) simpleJobMapper.selectByExample(example);
        return jobList;
    }

    public List<SimpleJobDO> queryRunningJob(SimpleJobDO simpleJobDO){
        simpleJobDO.setStatus(SimpleJobEnum.STATUS.RUNNING.name());
        return queryJob(simpleJobDO);
    }

    public List<SimpleJobMonitorDO> queryParentJobStatus(SimpleJobDO simpleJob){
        final Example example = new Example(SimpleJobMonitorDO.class);
        final Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("jobName", simpleJob.getParentJobName());
        criteria.andEqualTo("status", "success");
        criteria.andCondition("DATE_FORMAT(stampDate,'%Y-%m-%d')=DATE_FORMAT(CURDATE(),'%Y-%m-%d')");
        return simpleJobMonitorMapper.selectByExample(example);
    }

    public List<SimpleJobMonitorDO> queryWaitingSimpleJob(SimpleJobDO simpleJob){
        final Example example = new Example(SimpleJobMonitorDO.class);
        final Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("parentJobName", simpleJob.getJobName());
        criteria.andEqualTo("status", "waiting");
        criteria.andCondition("DATE_FORMAT(inputDate,'%Y-%m-%d')=DATE_FORMAT(CURDATE(),'%Y-%m-%d')");
        return simpleJobMonitorMapper.selectByExample(example);
    }

    public boolean isParentWaiting(SimpleJobDO simpleJob){
        if (StringUtils.isBlank(simpleJob.getParentJobName())){
            return false;
        }

        List<SimpleJobMonitorDO> simpleJobMonitorDOS = queryParentJobStatus(simpleJob);
        if (null == simpleJobMonitorDOS || simpleJobMonitorDOS.isEmpty()){
            return true;
        } else {
            return false;
        }
    }

    public void insertJobMonitor(SimpleJobDO simpleJob, String isSuccess){
        final Example example = new Example(SimpleJobMonitorDO.class);
        example.createCriteria().andEqualTo("simpleJobId", simpleJob.getSimpleJobId())
                .andEqualTo("jobName", simpleJob.getJobName())
                .andCondition("DATE_FORMAT(inputDate,'%Y-%m-%d')=DATE_FORMAT(CURDATE(),'%Y-%m-%d')");
        List<SimpleJobMonitorDO> jobMonitors = simpleJobMonitorMapper.selectByExample(example);

        if (null == jobMonitors || jobMonitors.size() == 0) {
            SimpleJobMonitorDO simpleJobMonitor = new SimpleJobMonitorDO();
            simpleJobMonitor.setSimpleJobId(simpleJob.getSimpleJobId());
            simpleJobMonitor.setJobName(simpleJob.getJobName());
            simpleJobMonitor.setStatus(isSuccess);
            simpleJobMonitor.setInputDate(new Date());
            simpleJobMonitor.setParentJobName(simpleJob.getParentJobName());
            simpleJobMonitorMapper.insertSelective(simpleJobMonitor);
        } else {
            for (SimpleJobMonitorDO jobMonitor : jobMonitors) {
                jobMonitor.setStatus(isSuccess);
                simpleJobMonitorMapper.updateByPrimaryKeySelective(jobMonitor);
            }
        }
    }

}
