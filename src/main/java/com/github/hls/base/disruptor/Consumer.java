package com.github.hls.base.disruptor;

import com.github.hls.base.disruptor.info.CheckUpInInfo;
import com.github.hls.base.disruptor.info.HandleType;
import com.github.hls.utils.SimpleDBUtils;
import com.lmax.disruptor.WorkHandler;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.log4j.Logger;

import javax.sql.DataSource;


/**
 * 任务消费者
 *
 * @author sunlihuo
 */
public class Consumer implements WorkHandler<CheckUpInInfo> {
	private static final Logger logger = Logger.getLogger(Consumer.class);

	private DataSource dataSource;
	private DataSource quotaBossDataSource;
	
	public Consumer(DataSource dataSource, DataSource quotaBossDataSource) {
		this.dataSource = dataSource;
		this.quotaBossDataSource = quotaBossDataSource;
	}

	@Override
	public void onEvent(CheckUpInInfo info) throws Exception {
		String sql = "";
		DataSource realDatesource = this.dataSource;
		
		try {
			if (HandleType.BATCH_HANDLE == info.getHandleType()) {
				QueryRunner sqlRunner = new QueryRunner(realDatesource);
				sql = info.getBatchSql();
				sqlRunner.insertBatch(sql, new ScalarHandler<Long>(), info.getBatchParams());
			} else if (HandleType.CKUPIN_HANDLE == info.getHandleType()){
				sql = info.getCheckExistSql();
				if (SimpleDBUtils.checkIsExist(sql, realDatesource)) {
					// 大于零的场合执行update语句
					sql = info.getUpdateSql();
					SimpleDBUtils.update(sql, realDatesource);
				} else {
					// 不存在的场合执行insert语句
					sql = info.getInsertSql();
					SimpleDBUtils.insert(sql, realDatesource);
				}
				
			} else if (HandleType.DELETE_HANDLE == info.getHandleType()) {
				sql = info.getUpdateSql();
				SimpleDBUtils.update(sql, realDatesource);
			} else {
				logger.error("！！！！！！！！The data may be missing！！！！！！！！！！！");
			}

		} catch (Exception e) {
			logger.error("checkUpIn error", e);

		} finally {
			info.empty();
			info.getLatch().countDown();
		}
		
	}

}
