package com.github.hls.etl.base.disruptor;

import lombok.Data;

import java.util.concurrent.CountDownLatch;

@Data
public class DataDTO {
	/**任务id*/
	private Long id;
	/**校验是否存在*/
	private String checkExistSql;
	/**更新*/
	private String updateSql;
	/**插入*/
	private String insertSql;
	/**CountDownLatch*/
	private CountDownLatch latch;
	/**批量(0),校验_插入_更新(1),删除(2);*/
	private int handleType;
	/**批量入库SQL*/
	private String batchSql;
	/**批量入库参数*/
	private Object[][] batchParams;
	/**目标数据源*/
	private String targetDb;

	public void empty() {
		checkExistSql = null;
		updateSql = null;
		insertSql = null;
		id = null;
		batchSql = null;
		batchParams = null;
		targetDb = null;
	}

	@Override
	protected void finalize() throws Throwable {
		empty();
		super.finalize();
	}
}
