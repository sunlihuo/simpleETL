package com.github.hls.mapper;

import com.github.hls.domain.SimpleJobMonitorDO;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface SimpleJobMonitorMapper extends Mapper<SimpleJobMonitorDO> {
}
