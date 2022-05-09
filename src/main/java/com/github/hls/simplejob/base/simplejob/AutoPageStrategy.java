package com.github.hls.simplejob.base.simplejob;

import com.github.hls.simplejob.base.simplejob.base.SimpleJobStrategy;
import com.github.hls.simplejob.domain.SimpleETLDO;
import com.github.hls.simplejob.base.enums.HandleTypeEnum;
import com.github.hls.simplejob.utils.SimpleDBUtils;
import com.github.hls.simplejob.utils.SimpleETLUtils;
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
    public void doHandle(SimpleETLDO simpleJob, DataSource dataSource) {
        Integer offset = 0;
        Integer limit = 10000;

        if (SimpleETLUtils.sectionValueList == null || SimpleETLUtils.sectionValueList.size() == 0) {
            String selectSQL = simpleJob.getSelectSql();
            String sql = SimpleETLUtils.getSysValueReplaceSql(selectSQL);

            String countSql = SimpleETLUtils.getCountSql(sql);
            Integer count = SimpleDBUtils.queryCount(countSql, dataSource);
            autoPage(simpleJob, dataSource, sql, count, offset, limit);
            return;
        }

        for (Map<String, Object> sectionMap : SimpleETLUtils.sectionValueList) {
            log.info("参数:sectionMap:{}", sectionMap);
            String selectSQL = simpleJob.getSelectSql();
            String sysValueSelectSQL = SimpleETLUtils.getSysValueReplaceSql(selectSQL);
            String sql = SimpleETLUtils.getSectionValueReplaceSql(sysValueSelectSQL, sectionMap, 0);

            String countSql = SimpleETLUtils.getCountSql(sql);
            Integer count = SimpleDBUtils.queryCount(countSql, dataSource);
            autoPage(simpleJob, dataSource, sql, count, offset, limit);
        }
    }

    /**
     * 自动分页
     */
    private void autoPage(SimpleETLDO job, DataSource dataSource, String sql, Integer total, Integer offset, Integer limit) {
        if (total == 0) {
            log.error("jobId:{},jobName:{}, 没有可操作数据", job.getSimpleJobId(), job.getJobName());
            return;
        }
        if (offset > total) {
            return;
        }
        log.info("自动分页,Job:{},名称:{},total:{},offset:{},limit:{}", job.getSimpleJobId(), job.getJobName(), total, offset, limit);

        List<Map<String, Object>> resultList = SimpleDBUtils.queryListMapPage(sql, dataSource, offset, limit);

        if (HandleTypeEnum.正常.getCode().equals(job.getHandleType())) {
            //正常模式 校验 更新 插入
            doCheckUpIn(job, resultList);
        } else if (HandleTypeEnum.自动SQL.getCode().equals(job.getHandleType())) {
            doAutoCheckUpIn(job, resultList);
        } else if (HandleTypeEnum.批量.getCode().equals(job.getHandleType())) {
            doBatch(job, resultList, null);
        }

        offset += limit;
        //递归
        autoPage(job, dataSource, sql, total, offset, limit);
    }
}
