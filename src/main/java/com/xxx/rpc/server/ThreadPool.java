package com.xxx.rpc.server;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPool {
    private static volatile ThreadPoolExecutor threadPool = null;

    private ThreadPool() {
    }

    ;

    public static final ThreadPoolExecutor getInstance() {
        return INSTANCE.threadPool;
    }

    private static class INSTANCE {
        public static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(16, 16, 600L,
                TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(65536));
    }

}
