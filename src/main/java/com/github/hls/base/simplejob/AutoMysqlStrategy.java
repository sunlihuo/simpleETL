package com.github.hls.base.simplejob;

import com.github.hls.domain.SimpleJobDO;
import com.github.hls.utils.SimpleDBUtils;
import com.github.hls.utils.SimpleJobUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Service
public class AutoMysqlStrategy extends SimpleJobStrategy{

    @Resource
    private DataSource dataSource;

    public void handle(SimpleJobDO simpleJob){
        if (SimpleJobUtils.sectionList == null || SimpleJobUtils.sectionList.size() == 0){
            List<Map<String, Object>> resultList = SimpleDBUtils.queryListMap(simpleJob.getSelectSQL(), dataSource);
            doBatchOrSelUpIn(simpleJob, true, resultList, null);
        }

        for (Map<String, Object> sectionMap : SimpleJobUtils.sectionList) {
            String selectSQL = simpleJob.getSelectSQL();
            String sql = SimpleJobUtils.getReplaceSql(selectSQL, sectionMap, 0);

            List<Map<String, Object>> resultList = SimpleDBUtils.queryListMap(sql, dataSource);
            doBatchOrSelUpIn(simpleJob, true, resultList, sectionMap);
        }

    }
}
