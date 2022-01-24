package com.github.hls.simplejob.base.simplejob;

import com.github.hls.simplejob.base.simplejob.base.SimpleJobStrategy;
import com.github.hls.simplejob.domain.SimpleJobEntity;
import com.github.hls.simplejob.utils.SimpleDBUtils;
import com.github.hls.simplejob.utils.SimpleJobUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * 自动生成 插入或更新sql
 */
@Slf4j
@Service
public class AutoPageStrategy extends SimpleJobStrategy {

    @Override
    public void doHandle(SimpleJobEntity simpleJob, DataSource dataSource) {
        Integer offset = 0;
        Integer limit = 10000;

        if (SimpleJobUtils.sectionList == null || SimpleJobUtils.sectionList.size() == 0) {
            String selectSQL = simpleJob.getSelectSql();
            String sql = SimpleJobUtils.replaceSysParam(selectSQL);

            String countSql = SimpleJobUtils.getCountSql(sql);
            Integer count = SimpleDBUtils.queryCount(countSql, dataSource);
            autoPage(simpleJob, dataSource, sql, count, offset, limit);
            return;
        }

        for (Map<String, Object> sectionMap : SimpleJobUtils.sectionList) {
            log.info("参数:sectionMap:{}", sectionMap);
            String selectSQL = simpleJob.getSelectSql();
            String sqlSection = SimpleJobUtils.getReplaceSql(selectSQL, sectionMap, 0);
            String sql = SimpleJobUtils.replaceSysParam(sqlSection);

            String countSql = SimpleJobUtils.getCountSql(sql);
            Integer count = SimpleDBUtils.queryCount(countSql, dataSource);
            autoPage(simpleJob, dataSource, sql, count, offset, limit);
        }
    }

    /**
     * 自动分页
     */
    private void autoPage(SimpleJobEntity simpleJob, DataSource dataSource, String sql, Integer total, Integer offset, Integer limit) {
        if (total == 0) {
            log.error("jobId:{},jobName:{}, 没有可操作数据", simpleJob.getSimpleJobId(), simpleJob.getJobName());
            return;
        }
        if (offset > total) {
            return;
        }
        log.info("自动分页,Job:{},名称:{},total:{},offset:{},limit:{}", simpleJob.getSimpleJobId(), simpleJob.getJobName(), total, offset, limit);

        List<Map<String, Object>> resultList = SimpleDBUtils.queryListMapPage(sql, dataSource, offset, limit);
        if ("auto_mysql".equals(simpleJob.getSourceType())) {
            //自动生成 插入或更新sql
            doBatchOrSelUpIn(simpleJob, true, resultList, null);
        } else if("mysql".equals(simpleJob.getSourceType())) {
            //正常模式 校验 更新 插入
            doCheckUpIn(simpleJob, resultList);
        }
        offset += limit;
        //递归
        autoPage(simpleJob, dataSource, sql, total, offset, limit);
    }
}
