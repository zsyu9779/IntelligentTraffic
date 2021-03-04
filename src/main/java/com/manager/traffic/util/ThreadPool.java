package com.manager.traffic.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.*;

public class ThreadPool {

    private static ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("demo-pool-%d").build();

    private static ExecutorService pool = new ThreadPoolExecutor(5, 200,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());

    public void execute(Runnable task) {
        pool.execute(task);
    }
}
