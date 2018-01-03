package com.github.hls.base.task;

import com.github.hls.base.disruptor.Disruptor;
import com.github.hls.base.disruptor.Producer;
import com.github.hls.base.enums.SimpleJobEnum;
import com.github.hls.base.simplejob.SimpleJobStrategy;
import com.github.hls.domain.SimpleJobDO;
import com.github.hls.service.SimpleJobServer;
import com.github.hls.utils.DateUtils;
import com.github.hls.utils.SpringUtil;
import lombok.extern.log4j.Log4j;
import org.apache.rocketmq.common.message.Message;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.github.hls.utils.SimpleJobUtils.transList2Map;

@Service
@Log4j
public class SimpleJobTask{

    @Resource
    private SimpleJobServer simpleJobServer;
    @Resource
    private Disruptor disruptor;

    public boolean handleMessage(Message msg) {
        return true;
    }

    public boolean handleHttp(SimpleJobDO simpleJobDO) {
        final List<SimpleJobDO> simpleJobS = simpleJobServer.queryJob(simpleJobDO);
        if (simpleJobS == null || simpleJobS.size() == 0){
            log.error("simplejob is null");
            return false;
        }

        handleJob(simpleJobS);
        return true;
    }

    private void handleJob(List<SimpleJobDO> simpleJobList) {
        Producer producer = disruptor.getProducer();

        try {
            long countCurrent = System.currentTimeMillis();
            int i = 0;
            final Map<String, List<SimpleJobDO>> simpleJobMap = transList2Map(simpleJobList);

            log.info("begin JobThreadService list  = " + simpleJobMap.values().size());

            final Iterator<List<SimpleJobDO>> iterator = simpleJobMap.values().iterator();
            while (iterator.hasNext()){
                List<SimpleJobDO> jobList = iterator.next();
                for (SimpleJobDO simpleJob : jobList){
                    Long current = System.currentTimeMillis();
                    log.info("开始第"+ ++i +"个任务 jobId = " + simpleJob.getSimpleJobId() + " ; jobName = " + simpleJob.getJobName() + " ;SourceType="+simpleJob.getSourceType());

                    try {
                        String beanName = SimpleJobEnum.SOURCE_TYPE.valueOf(simpleJob.getSourceType()).getBeanName();
                        SimpleJobStrategy simpleJobStrategy = (SimpleJobStrategy) SpringUtil.getBean(beanName);
                        simpleJobStrategy.setProducer(producer);
                        simpleJobStrategy.handle(simpleJob);
                    } catch (Exception e) {
                       log.error("SimpleJobTask error", e);
                       if (!"Y".equalsIgnoreCase(simpleJob.getErrorGoOn())){
                           break;
                       }
                    }

                    log.info("结束第"+ i +"个任务 jobId = " + simpleJob.getSimpleJobId() + " ; jobName = " + simpleJob.getJobName() + " ;耗时 = " + DateUtils.dateDiff(current, System.currentTimeMillis()));
                }
                //一组任务完成
                simpleJobServer.insertSuccess(jobList.get(0));
                //每组完成后，依赖子任务触发
                simpleJobServer.handleWaitingSimpleJob(jobList.get(0));

            }

            log.info("end JobThreadService" + " ;耗时 = " + DateUtils.dateDiff(countCurrent, System.currentTimeMillis()));
        } catch (Exception e) {
            log.error("SimpleJobTask error", e);
        } finally {
            disruptor.drainAndHalt();
        }

    }


}
