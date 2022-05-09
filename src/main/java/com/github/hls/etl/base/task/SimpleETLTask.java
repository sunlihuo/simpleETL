package com.github.hls.etl.base.task;

import com.github.hls.etl.base.disruptor.Disruptor;
import com.github.hls.etl.base.disruptor.Producer;
import com.github.hls.etl.base.enums.HandleTypeEnum;
import com.github.hls.etl.base.etl.base.AbsSimpleETLStrategy;
import com.github.hls.etl.domain.SimpleETLDO;
import com.github.hls.etl.service.SimpleETLService;
import com.github.hls.etl.utils.DateUtils;
import com.github.hls.etl.utils.SimpleDBUtils;
import com.github.hls.etl.utils.SimpleETLUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.github.hls.etl.utils.SimpleETLUtils.transList2Map;


@Service
@Slf4j
public class SimpleETLTask {

    @Resource
    private AbsSimpleETLStrategy sectionValueStrategy;
    @Resource
    private AbsSimpleETLStrategy autoPageStrategy;
    @Resource
    private SimpleETLService etlService;
    @Resource
    private Disruptor disruptor;
    @Resource
    private DataSource datacenterDataSource;

    /**
     * 任务执行
     *
     * @param etl
     * @param admin 不为空时可以执行任何etl
     * @return
     */
    public boolean handleHttp(SimpleETLDO etl, String admin) {
        List<SimpleETLDO> sysValueRunningetlList = etlService.querySysValueRunningetl(etl);
        if (!CollectionUtils.isEmpty(sysValueRunningetlList)) {
            handleSysValue(sysValueRunningetlList);
        }

        final List<SimpleETLDO> etlList = etlService.queryRunningetl(etl, admin);
        if (CollectionUtils.isEmpty(etlList)) {
            log.error("etl is null");
            return false;
        }

        handleetl(etlList);
        return true;
    }

    /**
     * 全局参数
     *
     * @param etlList
     */
    public void handleSysValue(List<SimpleETLDO> etlList) {
        if (CollectionUtils.isEmpty(etlList)) {
            return;
        }

        SimpleETLUtils.clearSysParam();
        etlList.stream().forEach(m -> {
            List<Map<String, Object>> maps = SimpleDBUtils.queryListMap(m.getSelectSql(), datacenterDataSource);
            maps.stream().forEach(map -> {
                map.keySet().stream().forEach(key -> {
                    SimpleETLUtils.putSysParam(key, map.get(key) == null ? "" : String.valueOf(map.get(key)));
                });
            });
        });
    }

    public void handleetl(List<SimpleETLDO> etlList) {
        Producer producer = disruptor.getProducer();

        try {
            long countCurrent = System.currentTimeMillis();
            int i = 0;
            final Map<String, List<SimpleETLDO>> etlMap = transList2Map(etlList);
            log.info("开始执行任务{}", etlMap.values().size());

            final Iterator<List<SimpleETLDO>> iterator = etlMap.values().iterator();
            while (iterator.hasNext()) {
                List<SimpleETLDO> groupEtlList = iterator.next();

                for (SimpleETLDO etl : groupEtlList) {
                    Long current = System.currentTimeMillis();
                    log.info("开始第{}个任务,etlId:{},etlName:{},sourceType:{}", ++i, etl.getId(), etl.getName(), etl.getHandleType());
                    try {
                        if (HandleTypeEnum.分段_参数.getCode().equals(etl.getHandleType())) {
                            sectionValueStrategy.setProducer(producer);
                            sectionValueStrategy.handle(etl);
                        } else {
                            autoPageStrategy.setProducer(producer);
                            autoPageStrategy.handle(etl);
                        }

                    } catch (Exception e) {
                        log.error("etlTask error", e);
                        if (etl.getErrorGoOn() == 1) {
                            break;
                        }
                    }

                    log.info("结束第{}个任务, etlId:{},etlName:{},耗时:{}", i, etl.getId(), etl.getName(), DateUtils.dateDiff(current, System.currentTimeMillis()));
                    etlService.subtractStatus(etl);
                }
                SimpleETLUtils.sectionValueList.clear();
            }

            log.info("结束任务, 耗时:{}", DateUtils.dateDiff(countCurrent, System.currentTimeMillis()));
        } catch (Exception e) {
            log.error("etlTask error", e);
        } finally {
            disruptor.drainAndHalt();
        }
    }
}
