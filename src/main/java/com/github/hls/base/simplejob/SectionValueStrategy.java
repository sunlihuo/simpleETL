package com.github.hls.base.simplejob;

import com.github.hls.base.task.SimpleJobTask;
import com.github.hls.domain.SimpleJobDO;
import com.github.hls.utils.SimpleDBUtils;
import com.github.hls.utils.SimpleJobUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Service
public class SectionValueStrategy extends SimpleJobStrategy{

    @Resource
    private DataSource dataSource;

    public void handle(SimpleJobDO simpleJob){
        SimpleJobUtils.sectionList.clear();
        SimpleJobUtils.sectionList.addAll(SimpleDBUtils.queryListMap(simpleJob.getSelectSQL(), dataSource));
    }
}
