package com.github.hls.simplejob.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.hls.simplejob.domain.SimpleETLDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SimpleETLMapper extends BaseMapper<SimpleETLDO> {

}