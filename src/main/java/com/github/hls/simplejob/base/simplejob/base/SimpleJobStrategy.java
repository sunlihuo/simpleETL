package com.github.hls.simplejob.base.simplejob.base;


import com.github.hls.simplejob.base.disruptor.Producer;
import com.github.hls.simplejob.domain.SimpleJobEntity;
import com.github.hls.simplejob.utils.SimpleDBBatchUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.hls.simplejob.utils.SimpleJobUtils.getSectionValueReplaceSql;


@Slf4j
@Data
public abstract class SimpleJobStrategy {

    /**
     * 批量标识
     */
    private static final String BATCH_LOWERCASE_STR = "batch€_";
    /**
     * 批量标识
     */
    private static final String BATCH_UPPERCASE_STR = "BATCH€_";
    /**
     * 强制不更新标识
     */
    private static final String NOUP_STR = "noup€";

    @Resource
    private DataSource datacenterDataSource;
    @Resource
    private DataSource oldmainDataSource;
    @Resource
    private DataSource storeDataSource;

    /**
     * 多线程
     */
    private Producer producer;

    /**
     * 执行 按数据源 分次执行
     * @param simpleJob
     */
    public void handle(SimpleJobEntity simpleJob) {
        DataSource dataSource = this.getOldmainDataSource();
        String sourceData = simpleJob.getSourceDb();
        if (StringUtils.isEmpty(sourceData)) {
            dataSource = this.getOldmainDataSource();
        }
        String[] sourceDataArr = sourceData.split(",");
        for (String sourceDataStr : sourceDataArr) {
            if ("oldmain".equalsIgnoreCase(sourceDataStr)) {
                dataSource = this.getOldmainDataSource();
            } else if ("store".equalsIgnoreCase(sourceDataStr)) {
                dataSource = this.getStoreDataSource();
            } else if ("datacenter".equalsIgnoreCase(sourceDataStr)) {
                dataSource = this.getDatacenterDataSource();
            }

            doHandle(simpleJob, dataSource);
        }
    }

    /**
     * 子类实现具体执行方法
     * @param simpleJob
     * @param dataSource
     */
    public abstract void doHandle(SimpleJobEntity simpleJob, DataSource dataSource);

    /**
     * 校验存在 并更新或插入
     * @param job
     * @param recordList
     */
    public void doCheckUpIn(SimpleJobEntity job, List<Map<String, Object>> recordList) {
        if (null == recordList || recordList.size() == 0) {
            log.error("jobId:{},jobName:{}, 没有可操作数据", job.getSimpleJobId(), job.getJobName());
            return;
        }
        CountDownLatch latch = null;

        for (Map<String, Object> oneRecordMap : recordList) {
            if (null == latch) {
                latch = new CountDownLatch(recordList.size());
                log.info("开始 doCheckUpIn; jobId:{},jobName:{},count:{}",job.getSimpleJobId(), job.getJobName(), recordList.size());
            }

            String checkExistSQL = getSectionValueReplaceSql(job.getCheckExistSql(), oneRecordMap, null);
            String updateSql = job.getUpdateSql();
            if (StringUtils.isNotBlank(updateSql)) {
                updateSql = getSectionValueReplaceSql(updateSql, oneRecordMap, 0);
            }
            String insertSql = getSectionValueReplaceSql(job.getInsertSql(), oneRecordMap, 0);
            producer.sendUpIn(checkExistSQL, updateSql, insertSql, job.getSimpleJobId(), latch);

        }
        try {
            //清内存
            recordList.clear();
            log.info("doCheckUpIn set resultList.clear()");
            latch.await(120, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            log.error("latchFinal.await();",e);
        }

        log.info("结束 doCheckUpIn; jobId:{},jobName:{},count:{}",job.getSimpleJobId(), job.getJobName(), recordList.size());
    }

    /**
     * 校验存在 并更新或插入
     * 自动生成 更新或插入sql
     * @param job
     * @param recordList
     */
    public void doAutoCheckUpIn(SimpleJobEntity job, List<Map<String, Object>> recordList) {
        if (null == recordList || recordList.size() == 0) {
            log.error("jobId:{},jobName:{}, 没有可操作数据", job.getSimpleJobId(), job.getJobName());
            return;
        }

        String checkSql = job.getCheckExistSql();
        String table;
        String where;
        try {
            String[] sqls = checkSql.split("where");
            table = sqls[0].split("from")[1];
            where = sqls[1];
        } catch (Exception e) {
            String[] sqls = checkSql.split("WHERE");
            table = sqls[0].split("FROM")[1];
            where = sqls[1];
        }

        //生成id
        table = table.trim();
        //String tableId = table.substring(0, 1).toLowerCase() + table.substring(1, table.length()) + "Id";
        String tableId = "id";
        log.info("========目标表={};主健={}", table, tableId);

        Map<String, Object> map = recordList.get(0);

        String updateSql = job.getUpdateSql();

        StringBuilder upSql = new StringBuilder();
        int i = 0;

        if (NOUP_STR.equalsIgnoreCase(updateSql)) {
            log.debug("updateSql is " + updateSql);
        } else {
            //UPDATE 表名称 SET 列名称 = 新值 WHERE id in (select a.id from (select id from 表 where 列名称 = 某值)a)
            upSql = upSql.append("UPDATE ").append(table).append(" SET ");
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                if (i == 0) {
                    i++;
                    upSql.append(key).append("='#").append(key).append("#'");
                } else {
                    upSql.append(",").append(key).append("='#").append(key).append("#'");
                }
            }
            upSql.append(" WHERE ").append(tableId).append(" = (");
            upSql.append(" SELECT a.").append(tableId).append(" FROM (");
            upSql.append(" SELECT ").append(tableId).append(" FROM ").append(table).append(" WHERE ").append(where).append(" LIMIT 1 )a)");
        }


        //INSERT INTO table_name (列1, 列2,...) VALUES (值1, 值2,....)
        StringBuilder inSql = new StringBuilder("INSERT INTO ").append(table);
        StringBuilder columnSql =  new StringBuilder("(");
        StringBuilder valuesSql =  new StringBuilder(")VALUES(");
        i = 0;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            if (i == 0) {
                i++;
                columnSql.append(key);
                valuesSql.append("'#").append(key).append("#'");
            } else {
                columnSql.append(",").append(key);
                valuesSql.append(",").append("'#").append(key).append("#'");
            }
        }
        inSql.append(columnSql).append(valuesSql);
        inSql.append(")");

        job.setUpdateSql(upSql.toString());
        job.setInsertSql(inSql.toString());

        doCheckUpIn(job, recordList);
    }

    /**
     * 批量删除 并 批量插入
     */
    public void doBatch(SimpleJobEntity job, List<Map<String, Object>> resultList, Map<String, Object> sectionMap) {
        if (CollectionUtils.isEmpty(resultList)) {
            log.error("jobId:{},jobName:{}, 没有可操作数据", job.getSimpleJobId(), job.getJobName());
            return;
        }

        String batchSql = job.getBatchSql();
        //批量入库流程
        if (batchSql.contains(BATCH_UPPERCASE_STR) || batchSql.contains(BATCH_LOWERCASE_STR)) {
            try {
                String €_ = batchSql.split("€_")[1];
                String insertTable = €_.split("_€")[0];
                //batch€_表_isInsert='WHD'
                int count = 0;
                Pattern p = Pattern.compile("_");
                Matcher m = p.matcher(batchSql);
                while (m.find()) {
                    count++;
                }

                if (count >= 2) {
                    String deleteWhere = batchSql.split("_€")[1];
                    if (null != sectionMap) {
                        deleteWhere = getSectionValueReplaceSql(deleteWhere, sectionMap, 0);
                    }
                    String needWhere = deleteWhere.toUpperCase().contains("WHERE") ? " " : " WHERE ";
                    String deleteSql = "DELETE FROM " + insertTable + needWhere + deleteWhere;
                    log.info("deleteSql = {}", deleteSql);

                    CountDownLatch latch = new CountDownLatch(1);
                    producer.sendDel(deleteSql,job.getSimpleJobId(), latch);
                    try {
                        latch.await(120, TimeUnit.MINUTES);
                    } catch (InterruptedException e) {
                        log.error("latchFinal.await();",e);
                    }
                }

                SimpleDBBatchUtils.insertBatchByDisruptor(producer, resultList, insertTable);
            } catch (Exception e) {
                log.error("insertTable = checkSql.split(_)[1]; error", e);
            }
        } else {
            log.error("BatchSQL 格式错误参考: batch€_unimall_user_daily_€where table_source='oldmain'");
        }
    }




}
