package com.github.hls.simplejob.base.disruptor.info;

import lombok.Data;

import java.util.concurrent.CountDownLatch;

@Data
public class DataInfo {
	/**校验是否存在*/
	private String checkExistSql;
	/**更新*/
	private String updateSql;
	/**插入*/
	private String insertSql;
	/**任务id*/
	private Long skynetJobId;
	/**CountDownLatch*/
	private CountDownLatch latch;
	/**批量(0),校验_插入_更新(1),删除(2);*/
	private int handleType;
	/**批量入库SQL*/
	private String batchSql;
	/**批量入库参数*/
	private Object[][] batchParams;
	/**更新数据源*/
	private String upDateSource;

	public void empty() {
		checkExistSql = null;
		updateSql = null;
		insertSql = null;
		skynetJobId = null;
		batchSql = null;
		batchParams = null;
		upDateSource = null;
	}

	@Override
	protected void finalize() throws Throwable {
		empty();
		super.finalize();
	}
}
