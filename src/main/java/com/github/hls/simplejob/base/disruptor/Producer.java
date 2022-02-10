package com.github.hls.simplejob.base.disruptor;

import com.github.hls.simplejob.base.disruptor.info.DBTypeEnum;
import com.lmax.disruptor.RingBuffer;
import com.github.hls.simplejob.base.disruptor.info.DataInfo;

import java.util.concurrent.CountDownLatch;


/**
 * 任务发布者
 * @author sunlihuo
 *
 */
public class Producer {

	private final RingBuffer<DataInfo> ringBuffer;
	
	public Producer(RingBuffer<DataInfo> ringBuffer){
		this.ringBuffer = ringBuffer;
	}
	
	/**
	 * onData用来发布事件，每调用一次就发布一次事件
	 * 它的参数会用过事件传递给消费者
	 */
	public void onData(String checkExistSql, String updateSql, String insertSql, Long skynetJobId, CountDownLatch latch, int handleType, String batchSql, Object[][] batchParams){
		//可以把ringBuffer看做一个事件队列，那么next就是得到下面一个事件槽
		long sequence = ringBuffer.next();
		try {
			//用上面的索引取出一个空的事件用于填充（获取该序号对应的事件对象）
			DataInfo task = ringBuffer.get(sequence);
			//获取要通过事件传递的业务数据
			task.setCheckExistSql(checkExistSql);
			task.setUpdateSql(updateSql);
			task.setInsertSql(insertSql);
			task.setSkynetJobId(skynetJobId);
			task.setLatch(latch);
			task.setHandleType(handleType);
			task.setBatchSql(batchSql);
			task.setBatchParams(batchParams);
		} finally {
			//发布事件
			//注意，最后的 ringBuffer.publish 方法必须包含在 finally 中以确保必须得到调用；如果某个请求的 sequence 未被提交，将会堵塞后续的发布操作或者其它的 producer。
			ringBuffer.publish(sequence);
		}
	}
	public void onDataDel(String updateSql, Long skynetJobId, CountDownLatch latch){
		onData(null, updateSql, null, skynetJobId, latch, DBTypeEnum.删除.getCode(), null, null);
	}

	public void onUpInData(String checkExistSql, String updateSql, String insertSql, Long skynetJobId, CountDownLatch latch){
		onData(checkExistSql, updateSql, insertSql, skynetJobId, latch, DBTypeEnum.校验_插入_更新.getCode(), null, null);
	}

	public void onBatchData(String batchSql, Object[][] batchParams, CountDownLatch latch){
		onData(null, null, null, null, latch, DBTypeEnum.批量.getCode(), batchSql, batchParams);
	}
	
}
