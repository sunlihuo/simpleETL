package com.github.hls.utils;

import com.github.hls.base.disruptor.Producer;
import org.apache.log4j.Logger;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 快速批量入库工具类
 * @author sunlihuo
 *
 */
public class QueryRunnerUtils {
	private static final Logger logger = Logger.getLogger(QueryRunnerUtils.class);

	/**每几行分批入库*/
	private static final int ROWS = 10;
	

	public static void insertBatchByDisruptor(Producer producer, List<Map<String, Object>> resultList, String table, String dateSource) {
		int size = resultList.size();
		logger.info("insertBatchByDisruptor begin-------------------resultList--------------------- = "+size);
		logger.info("insertBatchByDisruptor -------------------table--------------------- = "+table);

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
			if (!valueMap.containsKey("isDelete")) {
				valueMap.put("isDelete", "NO");
			}
			
			if (!isInit) {
				insertFileds = valueMap.keySet().toArray(new String[]{});
				sql = SimpleDBUtils.buildInsertSQL(table, insertFileds);
				params  = new Object[ROWS][];
				isInit = true;
			}
			params[k] = valueMap.values().toArray(new Object[]{});

			k++;
			if (k == ROWS) {
				producer.onData(sql, params, latch, dateSource);
				k = 0;
				params  = new Object[ROWS][];
			} else if (i == (resultSize - 1)) {
				Object[][] LastParams = new Object[lastResultSize][];
				for (int j = 0; j < lastResultSize; j++) {
					LastParams[j] = params[j];
				}
				producer.onData(sql, LastParams, latch, dateSource);
			}
		}
		
		try {
			resultList.clear();
			logger.info("insertBatchByDisruptor QueryRunnerUtils insertBatchByDisruptor set resultList.clear();");
			
			latch.await(120, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			logger.error("latchFinal.await();",e);
		}
		
		logger.info("insertBatchByDisruptor end-------------------resultList--------------------- = "+size);
	}

	
}
