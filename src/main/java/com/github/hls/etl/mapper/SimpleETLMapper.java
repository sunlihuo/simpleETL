package com.github.hls.etl.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.hls.etl.domain.SimpleETLDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SimpleETLMapper extends BaseMapper<SimpleETLDO> {

}