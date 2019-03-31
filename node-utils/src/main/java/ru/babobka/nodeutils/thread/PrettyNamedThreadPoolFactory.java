package ru.babobka.nodeutils.thread;

import lombok.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public interface PrettyNamedThreadPoolFactory {

    static ExecutorService fixedThreadPool(@NonNull String poolName, int threadsNum, boolean isDaemon, int priority) {
        return Executors.newFixedThreadPool(threadsNum, new ThreadFactory() {
            private final AtomicInteger threadNum = new AtomicInteger();

            @Override
            public Thread newThread(Runnable runnable) {
                Thread thread = Executors.defaultThreadFactory().newThread(runnable);
                thread.setName(poolName + ":num " + threadNum.getAndIncrement() + ":id " + thread.getId());
                thread.setDaemon(isDaemon);
                thread.setPriority(priority);
                return thread;
            }
        });
    }

    static ExecutorService fixedDaemonThreadPool(@NonNull String poolName, int threadsNum) {
        return fixedThreadPool(poolName, threadsNum, true, Thread.NORM_PRIORITY);
    }

    static ExecutorService fixedHighPriorityDaemonThreadPool(@NonNull String poolName, int threadsNum) {
        return fixedThreadPool(poolName, threadsNum, true, Thread.MAX_PRIORITY);
    }

    static ExecutorService fixedDaemonThreadPool(@NonNull String poolName) {
        return fixedThreadPool(poolName, Runtime.getRuntime().availableProcessors(), true, Thread.NORM_PRIORITY);
    }

    static ExecutorService fixedThreadPool(@NonNull String poolName) {
        return fixedThreadPool(poolName, Runtime.getRuntime().availableProcessors(), false, Thread.NORM_PRIORITY);
    }

    static ExecutorService singleThreadPool(@NonNull String poolName) {
        return fixedThreadPool(poolName, 1, false, Thread.NORM_PRIORITY);
    }

}
