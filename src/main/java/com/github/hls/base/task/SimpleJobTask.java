package com.github.hls.base.task;

import com.github.hls.base.enums.SimpleJobEnum;
import com.github.hls.base.simplejob.MysqlStrategy;
import com.github.hls.base.simplejob.SimpleJobStrategy;
import com.github.hls.domain.SimpleJobDO;
import com.github.hls.utils.DateUtils;
import com.github.hls.utils.SimpleJobUtils;
import com.github.hls.utils.SpringUtil;
import lombok.extern.log4j.Log4j;

import java.util.*;

import static com.github.hls.utils.SimpleJobUtils.transList2Map;

@Log4j
public class SimpleJobTask implements Runnable{

    @Override
    public void run() {
        long countCurrent = System.currentTimeMillis();
        int i = 0;
        final Map<String, List<SimpleJobDO>> simpleJobMap = transList2Map(this.simpleJobList);

        log.info("begin JobThreadService list  = " + simpleJobMap.values().size());

        final Iterator<List<SimpleJobDO>> iterator = simpleJobMap.values().iterator();
        while (iterator.hasNext()){
            List<SimpleJobDO> jobList = iterator.next();
            for (SimpleJobDO simpleJob : jobList){
                Long current = System.currentTimeMillis();
                log.info("开始第"+ ++i +"个任务 jobId = " + simpleJob.getSimpleJobId() + " ; jobName = " + simpleJob.getJobName() + " ;SourceType="+simpleJob.getSourceType());

                String beanName = SimpleJobEnum.SOURCE_TYPE.valueOf(simpleJob.getSourceType()).getBeanName();
                SimpleJobStrategy simpleJobStrategy = (SimpleJobStrategy) SpringUtil.getBean(beanName);
                try {
                   simpleJobStrategy.handle(simpleJob);
                } catch (Exception e) {
                   log.error("SimpleJobTask error", e);
                   if (!"Y".equalsIgnoreCase(simpleJob.getErrorGoOn())){
                       break;
                   }
                }

                log.info("结束第"+ i +"个任务 jobId = " + simpleJob.getSimpleJobId() + " ; jobName = " + simpleJob.getJobName() + " ;耗时 = " + DateUtils.dateDiff(current, System.currentTimeMillis()));
            }
        }

        log.info("end JobThreadService" + " ;耗时 = " + DateUtils.dateDiff(countCurrent, System.currentTimeMillis()));

    }


    private List<SimpleJobDO> simpleJobList;

    public SimpleJobTask(List<SimpleJobDO> simpleJobList){
        this.simpleJobList = simpleJobList;
    }
}
