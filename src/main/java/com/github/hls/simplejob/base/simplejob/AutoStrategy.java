package com.github.hls.simplejob.base.simplejob;


import com.github.hls.simplejob.base.simplejob.base.SimpleJobStrategy;
import com.github.hls.simplejob.domain.SimpleJobEntity;
import com.github.hls.simplejob.utils.SimpleDBUtils;
import com.github.hls.simplejob.utils.SimpleJobUtils;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * 自动生成 插入或更新sql
 */
@Service
public class AutoStrategy extends SimpleJobStrategy {

    @Override
    public void doHandle(SimpleJobEntity simpleJob, DataSource dataSource) {
        if (SimpleJobUtils.sectionList == null || SimpleJobUtils.sectionList.size() == 0) {
            String selectSQL = simpleJob.getSelectSql();
            String sql = SimpleJobUtils.replaceSysParam(selectSQL);

            List<Map<String, Object>> resultList = SimpleDBUtils.queryListMap(sql, dataSource);
            doBatchOrSelUpIn(simpleJob, true, resultList, null);
            return;
        }

        for (Map<String, Object> sectionMap : SimpleJobUtils.sectionList) {
            String selectSQL = simpleJob.getSelectSql();
            String sqlSection = SimpleJobUtils.getReplaceSql(selectSQL, sectionMap, 0);
            String sql = SimpleJobUtils.replaceSysParam(sqlSection);

            List<Map<String, Object>> resultList = SimpleDBUtils.queryListMap(sql, dataSource);
            doBatchOrSelUpIn(simpleJob, true, resultList, sectionMap);
        }
    }
}
