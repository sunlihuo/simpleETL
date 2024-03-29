package com.github.hls.etl.base.disruptor;


import com.github.hls.etl.base.task.BatchConsumerTask;
import com.github.hls.etl.utils.ThreadPoolUtil;
import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 高并发框架Disruptor
 *
 * @author sunlihuo
 *
 */
@Slf4j
@Component
public class Disruptor {
    private RingBuffer<DataDTO> ringBuffer;
    private static final int CONSUMER_SIZE = 20;
    private Producer producer;
    private WorkerPool<DataDTO> workerPool;
    private ExecutorService executor;
    /**引用记数*/
    private final AtomicInteger callCount = new AtomicInteger(0);

    public WorkerPool<DataDTO> getWorkerPool() {
        return workerPool;
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    @Resource
    private DataSource datacenterDataSource;
    @Resource
    private BatchConsumerTask batchConsumerTask;

    /**
     * 几次getProducer 要对应几次 drainAndHalt
     * @return
     */
    public Producer getProducer() {
        if (!workerPool.isRunning()) {
            log.info("==========disruptor.start()============");
            workerPool.start(executor);

            batchConsumerTask.start();
        }
        callCount.getAndIncrement();
        log.info("==========disruptor is started============callCount= " + callCount.get());
        return producer;
    }

    /**
     * 几次getProducer 要对应几次 drainAndHalt
     */
    public void drainAndHalt(){
        int i = callCount.get();
        callCount.getAndDecrement();
        if (i > 1) {
            log.info("==========disruptor. has other call============ callCount = " + i);
            return;
        }

        workerPool.drainAndHalt();
        batchConsumerTask.stop();
        BatchConsumerTask.runflag = false;
        log.info("==========disruptor.drainAndHalt()============");
    }

    public RingBuffer<DataDTO> getRingBuffer() {
        return ringBuffer;
    }

    @PostConstruct
    public void init() {
        // 创建缓冲池
        executor = ThreadPoolUtil.getExecutor();
        // 创建工厂
        EventFactory<DataDTO> factory = new EventFactory<DataDTO>() {
            @Override
            public DataDTO newInstance() {
                return new DataDTO();
            }
        };
        // 创建bufferSize ,也就是RingBuffer大小，必须是2的N次方
        int ringBufferSize = 1024;
        WaitStrategy WAIT_STRATEGY = new BlockingWaitStrategy();

        // 创建ringBuffer
        ringBuffer = RingBuffer.create(ProducerType.MULTI, factory, ringBufferSize, WAIT_STRATEGY);

        SequenceBarrier barriers = ringBuffer.newBarrier();

        Consumer[] consumers = new Consumer[CONSUMER_SIZE];
        for (int i = 0; i < consumers.length; i++) {
            consumers[i] = new Consumer(datacenterDataSource);
        }

        workerPool = new WorkerPool<>(ringBuffer, barriers,
                new IntEventExceptionHandler(), consumers);

        ringBuffer.addGatingSequences(workerPool.getWorkerSequences());
        producer = new Producer(ringBuffer);
    }

}
