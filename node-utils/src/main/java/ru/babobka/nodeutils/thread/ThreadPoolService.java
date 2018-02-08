package ru.babobka.nodeutils.thread;

import ru.babobka.nodeutils.container.Container;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by 123 on 23.09.2017.
 */
public abstract class ThreadPoolService<I extends Serializable, O extends Serializable> {
    private final int cores;
    private final Lock executionLock = new ReentrantLock();
    private final ExecutorService threadPool = Container.getInstance().get("service-thread-pool");
    private boolean stopped;

    public ThreadPoolService(int cores) {
        if (cores < 1) {
            throw new IllegalArgumentException("there must be at least one core");
        }
        this.cores = cores;
    }

    protected abstract void stopImpl();

    public synchronized void stop() {
        try {
            stopImpl();
        } finally {
            stopped = true;
        }
    }

    public O execute(I input) {
        if (input == null) {
            throw new IllegalArgumentException("input is null");
        } else if (isStopped()) {
            throw new IllegalStateException("service was stopped");
        } else if (!executionLock.tryLock()) {
            throw new IllegalStateException("task is already in use");
        }
        try {
            return executeImpl(input);
        } finally {
            executionLock.unlock();
        }
    }

    protected abstract O executeImpl(I input);

    protected synchronized <T> List<Future<T>> submit(List<? extends Callable<T>> callables) {
        if (callables == null) {
            throw new IllegalArgumentException("can not submit null callables");
        }
        List<Future<T>> futures = new ArrayList<>(callables.size());
        if (!threadPool.isShutdown()) {
            for (Callable<T> callable : callables) {
                futures.add(threadPool.submit(callable));
            }
        }
        return futures;
    }

    public synchronized boolean isStopped() {
        return stopped || Thread.currentThread().isInterrupted();
    }

    protected int getCores() {
        return cores;
    }

    public static ExecutorService createDaemonPool(int threads) {
        return Executors.newFixedThreadPool(threads,
                new ThreadFactory() {
                    public Thread newThread(Runnable r) {
                        Thread t = Executors.defaultThreadFactory().newThread(r);
                        t.setDaemon(true);
                        return t;
                    }
                });
    }

    public static ExecutorService createDaemonPool() {
        return createDaemonPool((int) (Runtime.getRuntime().availableProcessors() * 1.5));
    }

}
