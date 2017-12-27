package com.github.hls.base.simplejob;

import com.github.hls.domain.SimpleJobDO;
import com.github.hls.utils.SimpleDBUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Service
public class MysqlStrategy extends SimpleJobStrategy{

    @Resource
    private DataSource dataSource;

    public void handle(SimpleJobDO simpleJob){
        List<Map<String, Object>> resultList = SimpleDBUtils.queryListMap(simpleJob.getSelectSQL(), dataSource);
        doCheckUpIn(simpleJob, resultList);
    }
}
