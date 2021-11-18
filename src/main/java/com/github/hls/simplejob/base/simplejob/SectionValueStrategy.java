package com.github.hls.simplejob.base.simplejob;

import com.github.hls.simplejob.base.simplejob.base.SimpleJobStrategy;
import com.github.hls.simplejob.domain.SimpleJobEntity;
import com.github.hls.simplejob.utils.SimpleDBUtils;
import com.github.hls.simplejob.utils.SimpleJobUtils;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

/**
 * 分段sql
 * 查询出的结果作为内部变量赋值到同一个任务名的sql中
 * select id from table 取值
 * #id# 赋值
 */
@Service
public class SectionValueStrategy extends SimpleJobStrategy {

    @Override
    public void doHandle(SimpleJobEntity simpleJob, DataSource dataSource) {
        SimpleJobUtils.sectionList.clear();
        SimpleJobUtils.sectionList.addAll(SimpleDBUtils.queryListMap(simpleJob.getSelectSql(), dataSource));
    }
}
