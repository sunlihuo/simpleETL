package com.github.hls.base.simplejob;

import com.github.hls.base.simplejob.base.AutoStrategy;
import com.github.hls.base.simplejob.base.SimpleJobStrategy;
import com.github.hls.domain.SimpleJobDO;
import com.github.hls.utils.SimpleDBUtils;
import com.github.hls.utils.SimpleJobUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Service
public class AutoMysqlStrategy extends AutoStrategy {

    @Resource
    private DataSource dataSource;

    @PostConstruct
    public void init(){
        super.setDataSource(dataSource);
    }
}
