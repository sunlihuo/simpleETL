package com.github.hls.etl.base.etl;

import com.github.hls.etl.base.etl.base.AbsSimpleETLStrategy;
import com.github.hls.etl.domain.SimpleETLDO;
import com.github.hls.etl.base.enums.HandleTypeEnum;
import com.github.hls.etl.utils.SimpleDBUtils;
import com.github.hls.etl.utils.SimpleETLUtils;
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
public class AutoPageStrategy extends AbsSimpleETLStrategy {

    @Override
    public void doHandle(SimpleETLDO etl, DataSource dataSource) {
        Integer autoPageLimit = etl.getAutoPageLimit();
        Integer offset = 0;


        if (SimpleETLUtils.sectionValueList == null || SimpleETLUtils.sectionValueList.size() == 0) {
            String selectSQL = etl.getSelectSql();
            String sql = SimpleETLUtils.getSysValueReplaceSql(selectSQL);

            Integer total = 1000;
            if (autoPageLimit > 0) {
                String countSql = SimpleETLUtils.getCountSql(sql);
                total = SimpleDBUtils.queryCount(countSql, dataSource);
            }
            autoPage(etl, dataSource, sql, total, offset, autoPageLimit);

            return;
        }

        for (Map<String, Object> sectionMap : SimpleETLUtils.sectionValueList) {
            log.info("参数:sectionMap:{}", sectionMap);
            String selectSQL = etl.getSelectSql();
            String sysValueSelectSQL = SimpleETLUtils.getSysValueReplaceSql(selectSQL);
            String sql = SimpleETLUtils.getSectionValueReplaceSql(sysValueSelectSQL, sectionMap, 0);

            Integer total = 1000;
            if (autoPageLimit > 0) {
                String countSql = SimpleETLUtils.getCountSql(sql);
                total = SimpleDBUtils.queryCount(countSql, dataSource);
            }
            autoPage(etl, dataSource, sql, total, offset, autoPageLimit);
        }
    }

    /**
     * 自动分页
     */
    private void autoPage(SimpleETLDO etl, DataSource dataSource, String sql, Integer total, Integer offset, Integer autoPageLimit) {
        List<Map<String, Object>> resultList = null;
        if (autoPageLimit > 0) {
            if (total == 0) {
                log.error("etlId:{},etlName:{}, 没有可操作数据", etl.getId(), etl.getName());
                return;
            }
            if (offset > total) {
                return;
            }
            log.info("自动分页,etl:{},名称:{},total:{},offset:{},limit:{}", etl.getId(), etl.getName(), total, offset, autoPageLimit);
            resultList = SimpleDBUtils.queryListMapPage(sql, dataSource, offset, autoPageLimit);
        } else {
            resultList = SimpleDBUtils.queryListMap(sql, dataSource);
        }

        if (HandleTypeEnum.正常.getCode().equals(etl.getHandleType())) {
            //正常模式 校验 更新 插入
            doCheckUpIn(etl, resultList);
        } else if (HandleTypeEnum.自动SQL.getCode().equals(etl.getHandleType())) {
            doAutoCheckUpIn(etl, resultList);
        } else if (HandleTypeEnum.批量.getCode().equals(etl.getHandleType())) {
            doBatch(etl, resultList, null);
        }

        if (autoPageLimit > 0) {
            offset += autoPageLimit;
            //递归
            autoPage(etl, dataSource, sql, total, offset, autoPageLimit);
        }
    }
}
