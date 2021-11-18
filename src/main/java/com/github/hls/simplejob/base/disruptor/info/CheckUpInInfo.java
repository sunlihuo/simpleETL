package com.github.hls.simplejob.base.disruptor.info;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.CountDownLatch;

@Getter@Setter
public class CheckUpInInfo {
	private String checkExistSql;//检查更新的
	private String updateSql;//更新
	private String insertSql;//插入
	private Long skynetJobId;
	private CountDownLatch latch;
	private int handleType;
	private String batchSql;//批量入库SQL
	private Object[][] batchParams;//批量入库参数
	private String upDateSource;//更新数据源

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
