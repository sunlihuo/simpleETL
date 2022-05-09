package com.github.hls.etl.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.hls.etl.base.enums.HandleTypeEnum;
import com.github.hls.etl.domain.SimpleETLDO;
import com.github.hls.etl.mapper.SimpleETLMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class SimpleETLService extends ServiceImpl<SimpleETLMapper, SimpleETLDO> {


    /**
     * 查询需要执行的任务
     * @param etl
     * @param admin
     * @return
     */
    public List<SimpleETLDO> queryRunningetl(SimpleETLDO etl, String admin){
        LambdaQueryWrapper<SimpleETLDO> query = Wrappers.lambdaQuery();
        query.eq(etl.getId()!= null, SimpleETLDO::getId, etl.getId());
        query.eq(etl.getName()!= null, SimpleETLDO::getName, etl.getName());
        if (admin == null) {
            query.ne(SimpleETLDO::getStatus, 0);
        }
        query.ne(SimpleETLDO::getHandleType, HandleTypeEnum.全局_参数.getCode());
        query.orderByAsc(SimpleETLDO::getName, SimpleETLDO::getExecuteOrder, SimpleETLDO::getGmtCreate);
        List<SimpleETLDO> etlList = this.list(query);
        return etlList;
    }

    /**
     * 查询全局参数任务
     * @param etl
     * @return
     */
    public List<SimpleETLDO> querySysValueRunningetl(SimpleETLDO etl){
        LambdaQueryWrapper<SimpleETLDO> query = Wrappers.lambdaQuery();
        query.eq(etl.getId()!= null, SimpleETLDO::getId, etl.getId());
        query.eq(etl.getName()!= null, SimpleETLDO::getName, etl.getName());
        query.ne(SimpleETLDO::getStatus, 0);
        query.eq(SimpleETLDO::getHandleType, HandleTypeEnum.全局_参数.getCode());
        query.orderByAsc(SimpleETLDO::getName, SimpleETLDO::getExecuteOrder, SimpleETLDO::getGmtCreate);
        List<SimpleETLDO> etlList = this.list(query);
        return etlList;
    }

    /**
     * 减一次使用
     * -1为永远执行
     * 0不执行
     * 2表示可执行2次
     * @param etl
     */
    public void subtractStatus(SimpleETLDO etl){
        if (etl.getStatus().intValue() >= 1) {
            Integer status = etl.getStatus();
            status--;
            etl.setStatus(status);
        }
        etl.setGmtRunning(LocalDateTime.now());
        this.updateById(etl);
    }

}
