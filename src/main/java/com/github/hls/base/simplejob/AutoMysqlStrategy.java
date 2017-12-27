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
        for (Map<String, Object> map : SimpleJobUtils.sectionList) {
            String selectSQL = simpleJob.getSelectSQL();
            String sql = SimpleJobUtils.getReplaceSql(selectSQL, map, 0);

            List<Map<String, Object>> resultList = SimpleDBUtils.queryListMap(sql, dataSource);
            Map<String, Object> sectionMap = null;
            doBatchOrSelUpIn(simpleJob, true, resultList, sectionMap);
        }

    }
}
