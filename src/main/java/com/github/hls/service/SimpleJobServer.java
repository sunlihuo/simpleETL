package com.github.hls.service;

import com.github.hls.base.task.SimpleJobTask;
import com.github.hls.domain.SimpleJobDO;
import com.github.hls.domain.SimpleJobMonitorDO;
import com.github.hls.mapper.SimpleJobMapper;
import com.github.hls.mapper.SimpleJobMonitorMapper;
import org.apache.commons.lang.StringUtils;
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
    @Resource
    private SimpleJobTask simpleJobTask;

    public List<SimpleJobDO> queryJob(SimpleJobDO simpleJobDO){
        final Example example = new Example(SimpleJobDO.class);
        final Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("simpleJobId", simpleJobDO.getSimpleJobId());
        criteria.andEqualTo("jobName", simpleJobDO.getJobName());
        criteria.andEqualTo("status", "RUNING");
        example.orderBy("jobName");
        example.orderBy("executeOrder");
        List<SimpleJobDO> jobList = simpleJobMapper.selectByExample(example);
        return jobList;
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

    public void handleWaitingSimpleJob(SimpleJobDO simpleJob){
        //依赖子任务触发
        final List<SimpleJobMonitorDO> simpleJobMonitorDOS = queryWaitingSimpleJob(simpleJob);
        //发mq

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
            simpleJobMonitorMapper.insertSelective(simpleJobMonitor);
        } else {
            for (SimpleJobMonitorDO jobMonitor : jobMonitors) {
                jobMonitor.setStatus(isSuccess);
                simpleJobMonitorMapper.updateByPrimaryKeySelective(jobMonitor);
            }
        }
    }

}
