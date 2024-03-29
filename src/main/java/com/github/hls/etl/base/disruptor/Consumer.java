package com.github.hls.etl.base.disruptor;

import com.github.hls.etl.utils.SimpleDBBatchUtils;
import com.lmax.disruptor.WorkHandler;
import com.github.hls.etl.utils.SimpleDBUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;


import javax.sql.DataSource;
import java.sql.Connection;


/**
 * 任务消费者
 *
 * @author sunlihuo
 */
@Slf4j
public class Consumer implements WorkHandler<DataDTO> {

	private DataSource datacenterDataSource;

	
	public Consumer(DataSource datacenterDataSource) {
		this.datacenterDataSource = datacenterDataSource;
	}

	@Override
	public void onEvent(DataDTO info) throws Exception {
		/**切换目标数据源*/
		//String targetDb = info.getTargetDb();

		String sql = "";
		DataSource targetDatesource = this.datacenterDataSource;

		try {
			if (ETLTypeEnum.ETL_BATCH.getCode() == info.getEtlType()) {
				QueryRunner sqlRunner = new QueryRunner(targetDatesource);
				sql = info.getBatchSql();
				sqlRunner.insertBatch(sql, new ScalarHandler<Long>(), info.getBatchParams());
			} else if (ETLTypeEnum.ETL.getCode() == info.getEtlType()){
				sql = info.getCheckExistSql();
				if (SimpleDBUtils.checkIsExist(sql, targetDatesource)) {
					// 大于零的场合执行update语句
					sql = info.getUpdateSql();
					//SimpleDBUtils.update(sql, targetDatesource);
					SimpleDBBatchUtils.updateBatchQueue.add(sql);
				} else {
					// 不存在的场合执行insert语句
					sql = info.getInsertSql();
					//SimpleDBUtils.insert(sql, targetDatesource);
					SimpleDBBatchUtils.insertBatchQueue.add(sql);
				}
				
			} else if (ETLTypeEnum.ETL_DEL.getCode() == info.getEtlType()) {
				sql = info.getUpdateSql();
				SimpleDBUtils.update(sql, targetDatesource);
			} else {
				log.error("！！！！！！！！The data may be missing！！！！！！！！！！！");
			}

		} catch (Exception e) {
			log.error("checkUpIn error", e);

		} finally {
			info.empty();
			info.getLatch().countDown();
		}
		
	}

}
