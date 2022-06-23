package com.github.hls.etl.utils;

import com.github.hls.etl.base.disruptor.Producer;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 快速批量入库工具类
 *
 * @author sunlihuo
 */
@Slf4j
public class SimpleDBBatchUtils {
    /**
     * 每几行分批入库
     */
    private static final int ROWS = 10;

    public static final Queue<String> insertBatchQueue = new ConcurrentLinkedQueue();
    public static final Queue<String> updateBatchQueue = new ConcurrentLinkedQueue();

    public static void insertBatchByDisruptor(Producer producer, List<Map<String, Object>> resultList, String table) {
        int size = resultList.size();
        log.info("批量入库:{}, table:{}", size, table);

        String sql = "";
        Object[][] params = null;
        boolean isInit = false;
        int k = 0;
        int resultSize = resultList.size();
        int lastResultSize = resultSize % ROWS;

        int taskCount = resultSize / ROWS;
        CountDownLatch latch = null;
        if (lastResultSize == 0) {
            latch = new CountDownLatch(taskCount);
        } else {
            latch = new CountDownLatch(taskCount + 1);
        }

        String[] insertFileds = null;
        for (int i = 0; i < resultList.size(); i++) {
            Map<String, Object> valueMap = resultList.get(i);

            if (!isInit) {
                insertFileds = valueMap.keySet().toArray(new String[]{});
                sql = SimpleDBUtils.buildInsertSQL(table, insertFileds);
                params = new Object[ROWS][];
                isInit = true;
            }
            params[k] = valueMap.values().toArray(new Object[]{});

            k++;
            if (k == ROWS) {
                producer.sendETLBatch(sql, params, latch);
                k = 0;
                params = new Object[ROWS][];
            } else if (i == (resultSize - 1)) {
                Object[][] LastParams = new Object[lastResultSize][];
                for (int j = 0; j < lastResultSize; j++) {
                    LastParams[j] = params[j];
                }
                producer.sendETLBatch(sql, LastParams, latch);
            }
        }

        try {
            resultList.clear();
            log.info("批量入库完成:{}, table:{}, resultList.clear()", size, table);
            latch.await(120, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            log.error("latchFinal.await();", e);
        }

        log.info("批量入库完成:{}, table:{}", size, table);
    }

    public static void insertBatchJDBC(DataSource targetDatesource) {

        Statement stmt = null;
        Connection conn = null;
        try {
            if (insertBatchQueue.size() > 0) {
                conn = targetDatesource.getConnection();
                stmt = conn.createStatement();
                for (int i = 1; i < 10; i++) {
                    String sql = insertBatchQueue.poll();
                    if (sql != null) {
                        stmt.addBatch(sql);
                        log.debug("读到sql:{}", sql);
                    }
                }
                stmt.executeBatch();
                stmt.clearBatch();
            }
        } catch (SQLException ex) {
            log.info("批量入库JDBC异常", ex);
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                log.info("批量入库JDBC.close()异常", e);
            }
        }
    }

    public static void updateBatchJDBC(DataSource targetDatesource) {
        Statement stmt = null;
        Connection conn = null;
        try {
            if (updateBatchQueue.size() > 0) {
                conn = targetDatesource.getConnection();
                stmt = conn.createStatement();
                for (int i = 1; i < 10; i++) {
                    String sql = updateBatchQueue.poll();
                    if (sql != null) {
                        stmt.addBatch(sql);
                        log.debug("读到sql:{}", sql);
                    }
                }
                stmt.executeBatch();
                stmt.clearBatch();
            }
        } catch (SQLException ex) {
            log.info("批量入库JDBC异常", ex);
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                log.info("批量入库JDBC.close()异常", e);
            }
        }
    }
}
