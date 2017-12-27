package com.github.hls.base.disruptor;

import com.github.hls.base.disruptor.info.CheckUpInInfo;
import com.github.hls.base.disruptor.info.HandleType;
import com.lmax.disruptor.RingBuffer;

import java.util.concurrent.CountDownLatch;


/**
 * 任务发布者
 * @author sunlihuo
 *
 */
public class Producer {

	private final RingBuffer<CheckUpInInfo> ringBuffer;
	
	public Producer(RingBuffer<CheckUpInInfo> ringBuffer){
		this.ringBuffer = ringBuffer;
	}
	
	/**
	 * onData用来发布事件，每调用一次就发布一次事件
	 * 它的参数会用过事件传递给消费者
	 */
	public void onData(String checkExistSql, String updateSql, String insertSql, Long skynetJobId, CountDownLatch latch, int handleType, String batchSql, Object[][] batchParams, String dateSource){
		//可以把ringBuffer看做一个事件队列，那么next就是得到下面一个事件槽
		long sequence = ringBuffer.next();
		try {
			//用上面的索引取出一个空的事件用于填充（获取该序号对应的事件对象）
			CheckUpInInfo task = ringBuffer.get(sequence);
			//获取要通过事件传递的业务数据
			task.setCheckExistSql(checkExistSql);
			task.setUpdateSql(updateSql);
			task.setInsertSql(insertSql);
			task.setSkynetJobId(skynetJobId);
			task.setLatch(latch);
			task.setUpDateSource(dateSource);
			task.setHandleType(handleType);
			task.setBatchSql(batchSql);
			task.setBatchParams(batchParams);
		} finally {
			//发布事件
			//注意，最后的 ringBuffer.publish 方法必须包含在 finally 中以确保必须得到调用；如果某个请求的 sequence 未被提交，将会堵塞后续的发布操作或者其它的 producer。
			ringBuffer.publish(sequence);
		}
	}
	
	public void onData(String checkExistSql, String updateSql, String insertSql, Long skynetJobId, CountDownLatch latch, String dateSource){
		onData(checkExistSql, updateSql, insertSql, skynetJobId, latch, HandleType.CKUPIN_HANDLE, null, null, dateSource);
	}

	public void onData(String batchSql, Object[][] batchParams, CountDownLatch latch, String dateSource){
		onData(null, null, null, null, latch, HandleType.BATCH_HANDLE, batchSql, batchParams, dateSource);
	}
	
}
