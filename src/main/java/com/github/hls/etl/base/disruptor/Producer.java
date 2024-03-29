package com.github.hls.etl.base.disruptor;

import com.lmax.disruptor.RingBuffer;

import java.util.concurrent.CountDownLatch;


/**
 * 任务发布者
 * @author sunlihuo
 *
 */
public class Producer {

	private final RingBuffer<DataDTO> ringBuffer;
	
	public Producer(RingBuffer<DataDTO> ringBuffer){
		this.ringBuffer = ringBuffer;
	}
	
	/**
	 * onData用来发布事件，每调用一次就发布一次事件
	 * 它的参数会用过事件传递给消费者
	 */
	public void onData(String checkExistSql, String updateSql, String insertSql, Long id, CountDownLatch latch, int etlType, String batchSql, Object[][] batchParams){
		//可以把ringBuffer看做一个事件队列，那么next就是得到下面一个事件槽
		long sequence = ringBuffer.next();
		try {
			//用上面的索引取出一个空的事件用于填充（获取该序号对应的事件对象）
			DataDTO task = ringBuffer.get(sequence);
			//获取要通过事件传递的业务数据
			task.setCheckExistSql(checkExistSql);
			task.setUpdateSql(updateSql);
			task.setInsertSql(insertSql);
			task.setId(id);
			task.setLatch(latch);
			task.setEtlType(etlType);
			task.setBatchSql(batchSql);
			task.setBatchParams(batchParams);
		} finally {
			//发布事件
			//注意，最后的 ringBuffer.publish 方法必须包含在 finally 中以确保必须得到调用；如果某个请求的 sequence 未被提交，将会堵塞后续的发布操作或者其它的 producer。
			ringBuffer.publish(sequence);
		}
	}
	public void sendETLDel(String updateSql, Long id, CountDownLatch latch){
		onData(null, updateSql, null, id, latch, ETLTypeEnum.ETL_DEL.getCode(), null, null);
	}

	public void sendETL(String checkExistSql, String updateSql, String insertSql, Long id, CountDownLatch latch){
		onData(checkExistSql, updateSql, insertSql, id, latch, ETLTypeEnum.ETL.getCode(), null, null);
	}

	public void sendETLBatch(String batchSql, Object[][] batchParams, CountDownLatch latch){
		onData(null, null, null, null, latch, ETLTypeEnum.ETL_BATCH.getCode(), batchSql, batchParams);
	}
	
}
