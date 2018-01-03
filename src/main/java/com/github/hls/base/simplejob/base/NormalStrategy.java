package com.github.hls.base.simplejob.base;

import com.github.hls.domain.SimpleJobDO;
import com.github.hls.utils.SimpleDBUtils;

import java.util.List;
import java.util.Map;

public abstract class NormalStrategy extends SimpleJobStrategy {

    @Override
    public void handle(SimpleJobDO simpleJob){
        List<Map<String, Object>> resultList = SimpleDBUtils.queryListMap(simpleJob.getSelectSQL(), super.getDataSource());
        doCheckUpIn(simpleJob, resultList);
    }
}
