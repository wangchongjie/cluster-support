package com.baidu.unbiz.multiengine.cluster.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

public class CustomizableExecutors {

    public static final ExecutorService EXECUTOR_SERVICE =
            CustomizableExecutors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 4);

    public static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE =
            CustomizableExecutors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() * 4);

    private static long default_thread_timeout = 1;

    public static ExecutorService newFixedThreadPool(int nThreads) {
        return newFixedThreadPool(nThreads, "cluster.thread");
    }

    public static ExecutorService newFixedThreadPool(int nThreads, String name) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(1,
                nThreads,
                default_thread_timeout,
                TimeUnit.MINUTES,
                new LinkedBlockingQueue<Runnable>(),
                new CustomizableThreadFactory(name));
        // executor.allowCoreThreadTimeOut(true);
        return executor;
    }

    public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize) {
        return newScheduledThreadPool(corePoolSize, "report-engine.schedule");
    }

    public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize, String name) {
        ScheduledThreadPoolExecutor executor =
                new ScheduledThreadPoolExecutor(corePoolSize, new CustomizableThreadFactory(name));
        executor.setMaximumPoolSize(corePoolSize);
        executor.setKeepAliveTime(default_thread_timeout, TimeUnit.MINUTES);
        // executor.allowCoreThreadTimeOut(true);
        return executor;
    }

}
