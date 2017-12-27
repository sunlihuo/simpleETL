package com.github.hls.base.task;

import com.github.hls.base.enums.SimpleJobEnum;
import com.github.hls.base.simplejob.MysqlStrategy;
import com.github.hls.base.simplejob.SimpleJobStrategy;
import com.github.hls.domain.SimpleJobDO;
import com.github.hls.utils.SpringUtil;

import java.util.*;

public class SimpleJobTask implements Runnable{

    @Override
    public void run() {


        final Iterator<List<SimpleJobDO>> iterator = simpleJobMap.values().iterator();
        while (iterator.hasNext()){
            List<SimpleJobDO> jobList = iterator.next();
            for (SimpleJobDO simpleJob : jobList){
                String beanName = SimpleJobEnum.SOURCE_TYPE.valueOf(simpleJob.getSourceType()).getBeanName();
                SimpleJobStrategy simpleJobStrategy = (SimpleJobStrategy) SpringUtil.getBean(beanName);
                simpleJobStrategy.handle(simpleJob);
            }
        }

    }

    private SimpleJobStrategy simpleJobStrategy;
    private Map<String, List<SimpleJobDO>> simpleJobMap;

    public SimpleJobTask(Map<String, List<SimpleJobDO>> simpleJobMap, SimpleJobStrategy simpleJobStrategy){
        this.simpleJobMap = simpleJobMap;
        this.simpleJobStrategy = simpleJobStrategy;
    }
}
