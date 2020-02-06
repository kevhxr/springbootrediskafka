package com.hxr.springrediskafka.util;

import java.util.concurrent.*;

public class ExecutorPoolUtil {

    public static ExecutorService generateThreadPool() {
        return new ThreadPoolExecutor(3,
                3,
                5,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(50),
                new MyRejectHandler());
    }

    static class MyRejectHandler implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            try {
                executor.getQueue().put(r);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
