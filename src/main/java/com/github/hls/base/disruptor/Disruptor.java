package com.github.hls.base.disruptor;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import com.github.hls.base.disruptor.info.CheckUpInInfo;
import org.apache.log4j.Logger;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.WorkerPool;
import com.lmax.disruptor.dsl.ProducerType;


/**
 * 高并发框架Disruptor
 *
 * @author sunlihuo
 *
 */
public class Disruptor {
    private static final Logger logger = Logger.getLogger(Disruptor.class);
    private RingBuffer<CheckUpInInfo> ringBuffer;

    private static final int CONSUMER_SIZE = 10;
    private final Producer producer;
    private final WorkerPool<CheckUpInInfo> workerPool;
    private final ExecutorService executor;
    /**引用记数*/
    private final AtomicInteger callCount = new AtomicInteger(0);

    public WorkerPool<CheckUpInInfo> getWorkerPool() {
        return workerPool;
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    /**
     * 几次getProducer 要对应几次 drainAndHalt
     * @return
     */
    public Producer getProducer() {
        if (!workerPool.isRunning()) {
            logger.info("==========disruptor.start()============");
            workerPool.start(executor);
        }
        callCount.getAndIncrement();
        logger.info("==========disruptor is started============callCount= " + callCount.get());
        return producer;
    }

    /**
     * 几次getProducer 要对应几次 drainAndHalt
     */
    public void drainAndHalt(){
        int i = callCount.get();
        callCount.getAndDecrement();
        if (i > 1) {
            logger.info("==========disruptor. has other call============ callCount = " + i);
            return;
        }

        workerPool.drainAndHalt();
        logger.info("==========disruptor.drainAndHalt()============");
    }

    public RingBuffer<CheckUpInInfo> getRingBuffer() {
        return ringBuffer;
    }

    public Disruptor(DataSource dataSource, DataSource quotaBossDataSource) {
        // 创建缓冲池
        executor = Executors.newFixedThreadPool(CONSUMER_SIZE);
        // 创建工厂
        EventFactory<CheckUpInInfo> factory = new EventFactory<CheckUpInInfo>() {
            @Override
            public CheckUpInInfo newInstance() {
                return new CheckUpInInfo();
            }
        };
        // 创建bufferSize ,也就是RingBuffer大小，必须是2的N次方
        int ringBufferSize = 1024; //
        WaitStrategy WAIT_STRATEGY = new BlockingWaitStrategy();

        // 创建ringBuffer
        ringBuffer = RingBuffer.create(ProducerType.MULTI, factory, ringBufferSize, WAIT_STRATEGY);

        SequenceBarrier barriers = ringBuffer.newBarrier();

        Consumer[] consumers = new Consumer[CONSUMER_SIZE];
        for (int i = 0; i < consumers.length; i++) {
            consumers[i] = new Consumer(dataSource, quotaBossDataSource);
        }

        workerPool = new WorkerPool<CheckUpInInfo>(ringBuffer, barriers,
                new IntEventExceptionHandler(), consumers);

        ringBuffer.addGatingSequences(workerPool.getWorkerSequences());
        producer = new Producer(ringBuffer);
    }

}
