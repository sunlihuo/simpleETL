package com.github.hls.etl.base.simplejob;

import com.github.hls.etl.base.simplejob.base.SimpleETLStrategy;
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
public class SectionValueStrategy extends SimpleETLStrategy {

    @Override
    public void doHandle(SimpleETLDO simpleJob, DataSource dataSource) {
        SimpleETLUtils.sectionValueList.clear();
        if (StringUtils.isNotBlank(simpleJob.getSelectSql())) {
            SimpleETLUtils.sectionValueList.addAll(SimpleDBUtils.queryListMap(simpleJob.getSelectSql(), dataSource));
        }
    }
}
