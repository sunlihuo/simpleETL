package com.github.hls.mapper;

import com.github.hls.domain.SimpleJobDO;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface SimpleJobMapper extends Mapper<SimpleJobDO> {
}
