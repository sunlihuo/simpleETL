package com.github.hls.base.simplejob;

import com.github.hls.domain.SimpleJobDO;
import com.github.hls.utils.SimpleDBUtils;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

public class AutoMidMysqlStrategy extends SimpleJobStrategy{

    @Resource
    private DataSource midDataSource;

    public void handle(SimpleJobDO simpleJob){
        List<Map<String, Object>> resultList = SimpleDBUtils.queryListMap(simpleJob.getSelectSQL(), midDataSource);
        doAutoCheckUpIn(simpleJob, resultList);
    }
}
