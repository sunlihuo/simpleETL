package com.github.hls.simplejob.utils;

import com.github.hls.simplejob.base.disruptor.Producer;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 快速批量入库工具类
 * @author sunlihuo
 *
 */
@Slf4j
public class QueryRunnerUtils {
	/**每几行分批入库*/
	private static final int ROWS = 10;
	

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
//			if (!valueMap.containsKey("isDelete")) {
//				valueMap.put("isDelete", "NO");
//			}
			
			if (!isInit) {
				insertFileds = valueMap.keySet().toArray(new String[]{});
				sql = SimpleDBUtils.buildInsertSQL(table, insertFileds);
				params  = new Object[ROWS][];
				isInit = true;
			}
			params[k] = valueMap.values().toArray(new Object[]{});

			k++;
			if (k == ROWS) {
				producer.onBatchData(sql, params, latch);
				k = 0;
				params  = new Object[ROWS][];
			} else if (i == (resultSize - 1)) {
				Object[][] LastParams = new Object[lastResultSize][];
				for (int j = 0; j < lastResultSize; j++) {
					LastParams[j] = params[j];
				}
				producer.onBatchData(sql, LastParams, latch);
			}
		}
		
		try {
			resultList.clear();
			log.info("批量入库完成:{}, table:{}, resultList.clear()", size, table);
			latch.await(120, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			log.error("latchFinal.await();",e);
		}

		log.info("批量入库完成:{}, table:{}", size, table);
	}

	
}
