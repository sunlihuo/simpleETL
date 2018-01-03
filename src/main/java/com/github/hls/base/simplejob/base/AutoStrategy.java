package com.github.hls.base.simplejob.base;

import com.github.hls.domain.SimpleJobDO;
import com.github.hls.utils.SimpleDBUtils;
import com.github.hls.utils.SimpleJobUtils;

import java.util.List;
import java.util.Map;

public abstract class AutoStrategy extends SimpleJobStrategy {

    @Override
    public void handle(SimpleJobDO simpleJob){
        if (SimpleJobUtils.sectionList == null || SimpleJobUtils.sectionList.size() == 0){
            List<Map<String, Object>> resultList = SimpleDBUtils.queryListMap(simpleJob.getSelectSQL(), super.getDataSource());
            doBatchOrSelUpIn(simpleJob, true, resultList, null);
        }

        for (Map<String, Object> sectionMap : SimpleJobUtils.sectionList) {
            String selectSQL = simpleJob.getSelectSQL();
            String sql = SimpleJobUtils.getReplaceSql(selectSQL, sectionMap, 0);

            List<Map<String, Object>> resultList = SimpleDBUtils.queryListMap(sql, super.getDataSource());
            doBatchOrSelUpIn(simpleJob, true, resultList, sectionMap);
        }
    }
}
