package com.github.hls.etl.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * 线程池
 * @author sunlihuo
 */
public class ThreadPoolUtil {
    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolUtil.class);

    /**
     * 核心线程数
     */
    private static final int CORE_POOL_SIZE = 20;

    /**
     * 最大线程数
     */
    private static final int MAX_POOL_SIZE = 50;

    /**
     * 阻塞任务队列大小
     */
    public static final int QUEUE_CAPACITY = 64;

    /**
     * 空闲线程存活时间
     */
    public static final Long KEEP_ALIVE_TIME = 3L;

    private static final ExecutorService EXECUTOR;

    static {
        EXECUTOR = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME,
                TimeUnit.MINUTES, new ArrayBlockingQueue<>(QUEUE_CAPACITY), new ThreadFactoryBuilder()
                .setNameFormat("etl-thread-pool-%d")
                .setUncaughtExceptionHandler((thread, throwable) ->
                        logger.error("ThreadPool:{}, Throwable:{} got exception",
                                thread.toString(), throwable.toString())).build(),
                (r, executor) -> {
                    if (!executor.isShutdown()) {
                        executor.setMaximumPoolSize(executor.getMaximumPoolSize() + 1);
                        executor.execute(r);
                    }
                });

    }

    public static ExecutorService getExecutor(){
        return EXECUTOR;
    }

    public static void execute(Runnable task) {
        EXECUTOR.execute(task);
    }

    public static <T> Future<T> submit(Callable<T> callable, boolean isQuery) {
        if (isQuery) {
            return EXECUTOR.submit(callable);
        } else {
            return null;
        }
    }

    public static <T> Future<T> submit(Callable<T> callable) {
        return EXECUTOR.submit(callable);
    }

//    /**
//     * callable 获取集合数据
//     * @return              task中的数据
//     */
//    public static  <T> List<T> callableGetList(Future<List<T>> task) {
//        List<T> list;
//        try {
//            list = task.get(30, TimeUnit.SECONDS);
//        } catch (Exception e) {
//            throw new ServiceException("异步获取信息异常");
//        }
//        return list;
//    }
//
//    /**
//     * callable 获取对象
//     * @return  task中的数据
//     */
//    public static <T> T callableGetObject(Future<T> task) {
//        if (task == null) {
//            return (T) new ArrayList<>();
//        }
//
//        T t;
//        try {
//            t = task.get(30, TimeUnit.SECONDS);
//        } catch (Exception e) {
//            throw new ServiceException("异步获取信息异常");
//        }
//        return t;
//    }

    /**
     * 禁止创建对象
     */
    private ThreadPoolUtil(){}
}
