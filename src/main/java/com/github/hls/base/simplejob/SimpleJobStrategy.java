package com.github.hls.base.simplejob;

import com.github.hls.base.disruptor.Producer;
import com.github.hls.domain.SimpleJobDO;
import com.github.hls.utils.QueryRunnerUtils;
import com.github.hls.utils.SimpleDBUtils;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang.StringUtils;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.hls.utils.SimpleJobUtils.getReplaceSql;

@Log4j
public abstract class SimpleJobStrategy {

    private static final String BATCH_LOWERCASE_STR = "batch€_";
    private static final String BATCH_UPPERCASE_STR = "BATCH€_";

    protected final List<Map<String, Object>> sectionList = new ArrayList<>();
    private Producer producer;

    public void handle(MysqlStrategy mysqlSimpleJob, SimpleJobDO simpleJob){
        mysqlSimpleJob.handle(simpleJob);
    }

    public void handle(AutoMysqlStrategy mysqlSimpleJob, SimpleJobDO simpleJob){
        mysqlSimpleJob.handle(simpleJob);
    }

    
    protected void doCheckUpIn(SimpleJobDO job, List<Map<String, Object>> recordList) {
        if (null == recordList || recordList.size() == 0) {
            log.error(" jobNme = " + job.getJobName() + " ;jobId = " + job.getSimpleJobId() + " recordList is null");
            return;
        }
        CountDownLatch latch = null;

        for (Map<String, Object> oneRecordMap : recordList) {
            if (null == latch) {
                latch = new CountDownLatch(recordList.size());
                log.info("begin doCheckUpIn ; jobNme = " + job.getJobName() + " ;jobId = " + job.getSimpleJobId() + latch);
            }

            String checkExistSQL = getReplaceSql(job.getCheckExistSQL(), oneRecordMap, null);
            String updateSql = job.getUpdateSQL();
            if (StringUtils.isNotBlank(updateSql)) {
                updateSql = getReplaceSql(updateSql, oneRecordMap, 0);
            }
            String insertSql = getReplaceSql(job.getInsertSQL(), oneRecordMap, 0);
            producer.onData(checkExistSQL, updateSql, insertSql, job.getSimpleJobId(), latch, job.getUpDateSource());

        }
        try {
            //清内存
            recordList.clear();
            log.info("doCheckUpIn set resultList.clear()");
            latch.await(120, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            log.error("latchFinal.await();",e);
        }
        
        log.info("end doCheckUpIn ; jobNme = " + job.getJobName() + " ;jobId = " + job.getSimpleJobId() + latch);
    }

    private void doAutoCheckUpIn(SimpleJobDO job, List<Map<String, Object>> recordList) {
        if (null == recordList || recordList.size() == 0) {
            log.error(" jobNme = " + job.getJobName() + " ;jobId = " + job.getSimpleJobId() + " recordList is null");
            return;
        }

        String checkSql = job.getCheckExistSQL();
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
        String tableId = table.substring(0, 1).toLowerCase() + table.substring(1, table.length()) + "Id";
        log.info("========table="+table+";tableId="+tableId);

        Map<String, Object> map = recordList.get(0);

        String updateSql = job.getUpdateSQL();

        StringBuilder upSql = new StringBuilder();
        int i = 0;
        if (StringUtils.isBlank(updateSql)) {
            log.debug("updateSql is " + updateSql);
        } else {
            //UPDATE 表名称 SET 列名称 = 新值 WHERE id in (select a.id from (select id from 表 where 列名称 = 某值)a)
            upSql = upSql.append("UPDATE ").append(table).append(" SET ");
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                if (i == 0) {
                    i++;
                    upSql.append("updateTime=").append("SYSDATE(),");
                    upSql.append("isDelete=").append("'NO',");
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
                columnSql.append("inputDate,");
                valuesSql.append("SYSDATE(),");
                columnSql.append("isDelete,");
                valuesSql.append("'NO',");
                columnSql.append(key);
                valuesSql.append("'#").append(key).append("#'");
            } else {
                columnSql.append(",").append(key);
                valuesSql.append(",").append("'#").append(key).append("#'");
            }
        }
        inSql.append(columnSql).append(valuesSql);
        inSql.append(")");

        job.setUpdateSQL(upSql.toString());
        job.setInsertSQL(inSql.toString());

        doCheckUpIn(job, recordList);
    }

    protected void doBatchOrSelUpIn(SimpleJobDO job, boolean isAuto, List<Map<String, Object>> resultList, Map<String, Object> sectionMap) {

        if (null == resultList || resultList.size() == 0) {
            log.error("jobId="+job.getSimpleJobId()+",JobName="+job.getJobName()+"recordList is null");

            return;
        }

        String checkSql = job.getCheckExistSQL();
        String insertTable = "";
        String deleteWhere = "";
        //批量入库流程
        if (checkSql.contains(BATCH_UPPERCASE_STR) || checkSql.contains(BATCH_LOWERCASE_STR)) {
            try {
                insertTable = checkSql.split("_")[1];
                //batch€_表_isInsert='WHD'
                int count = 0;
                Pattern p = Pattern.compile("_");
                Matcher m = p.matcher(checkSql);
                while (m.find()) {
                    count++;
                }

                if (count >= 2) {
                    deleteWhere = checkSql.substring(checkSql.indexOf("_") + 1);
                    deleteWhere = deleteWhere.substring(deleteWhere.indexOf("_") + 1);
                    if (null != sectionMap) {
                        deleteWhere = getReplaceSql(deleteWhere, sectionMap, 0);
                    }
                    String needWhere = deleteWhere.toUpperCase().contains("WHERE") ? " " : " WHERE ";
                    String deleteSql = "DELETE FROM " + insertTable + needWhere + deleteWhere;
                    log.info("YqtDBUtils.update deleteSql = " + deleteSql);

                    CountDownLatch latch = new CountDownLatch(1);
                    producer.onData(null, deleteSql, null, job.getSimpleJobId(), latch, job.getUpDateSource());
                    try {
                        latch.await(120, TimeUnit.MINUTES);
                    } catch (InterruptedException e) {
                        log.error("latchFinal.await();",e);
                    }
                }

                QueryRunnerUtils.insertBatchByDisruptor(producer, resultList, insertTable ,job.getUpDateSource());
            } catch (Exception e) {
                log.error("insertTable = checkSql.split(_)[1]; error", e);
            }
        } else {
            //普通流程
            if (isAuto) {
                doAutoCheckUpIn(job, resultList);
            } else {
                doCheckUpIn(job, resultList);
            }
        }
    }



}
