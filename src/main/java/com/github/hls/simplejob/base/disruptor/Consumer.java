package com.github.hls.simplejob.base.disruptor;

import com.github.hls.simplejob.base.disruptor.info.DBTypeEnum;
import com.lmax.disruptor.WorkHandler;
import com.github.hls.simplejob.base.disruptor.info.DataInfo;
import com.github.hls.simplejob.utils.SimpleDBUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;


import javax.sql.DataSource;


/**
 * 任务消费者
 *
 * @author sunlihuo
 */
@Slf4j
public class Consumer implements WorkHandler<DataInfo> {

	private DataSource datacenterDataSource;

	
	public Consumer(DataSource datacenterDataSource) {
		this.datacenterDataSource = datacenterDataSource;
	}

	@Override
	public void onEvent(DataInfo info) throws Exception {
		String sql = "";
		DataSource targetDatesource = this.datacenterDataSource;
		
		try {
			if (DBTypeEnum.批量.getCode() == info.getHandleType()) {
				QueryRunner sqlRunner = new QueryRunner(targetDatesource);
				sql = info.getBatchSql();
				sqlRunner.insertBatch(sql, new ScalarHandler<Long>(), info.getBatchParams());
			} else if (DBTypeEnum.校验_插入_更新.getCode() == info.getHandleType()){
				sql = info.getCheckExistSql();
				if (SimpleDBUtils.checkIsExist(sql, targetDatesource)) {
					// 大于零的场合执行update语句
					sql = info.getUpdateSql();
					SimpleDBUtils.update(sql, targetDatesource);
				} else {
					// 不存在的场合执行insert语句
					sql = info.getInsertSql();
					SimpleDBUtils.insert(sql, targetDatesource);
				}
				
			} else if (DBTypeEnum.删除.getCode() == info.getHandleType()) {
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
