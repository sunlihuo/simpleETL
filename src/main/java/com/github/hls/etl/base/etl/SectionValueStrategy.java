package com.github.hls.etl.base.etl;

import com.github.hls.etl.base.etl.base.AbsSimpleETLStrategy;
import com.github.hls.etl.domain.SimpleETLDO;
import com.github.hls.etl.utils.SimpleDBUtils;
import com.github.hls.etl.utils.SimpleETLUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

/**
 * 分段sql
 * 查询出的结果作为内部变量赋值到同一个任务名的sql中
 * select id from table 取值
 * #id# 赋值
 */
@Service
public class SectionValueStrategy extends AbsSimpleETLStrategy {

    @Override
    public void doHandle(SimpleETLDO etl, DataSource dataSource) {
        SimpleETLUtils.sectionValueList.clear();
        if (StringUtils.isNotBlank(etl.getSelectSql())) {
            SimpleETLUtils.sectionValueList.addAll(SimpleDBUtils.queryListMap(etl.getSelectSql(), dataSource));
        }
    }
}
