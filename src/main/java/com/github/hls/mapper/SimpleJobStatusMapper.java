package com.github.hls.mapper;

import com.github.hls.domain.SimpleJobStatusDO;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository("simpleJobStatusMapper")
public interface SimpleJobStatusMapper extends Mapper<SimpleJobStatusDO> {
}
