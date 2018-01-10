package ru.babobka.nodeutils.thread;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by 123 on 23.09.2017.
 */
public abstract class ThreadPoolService<I extends Serializable, O extends Serializable> {
    private final int cores;
    private final Lock executionLock = new ReentrantLock();
    private final UUID serviceId = UUID.randomUUID();
    private ExecutorService threadPool;
    private boolean stopped;

    public ThreadPoolService(int cores) {
        if (cores < 1) {
            throw new IllegalArgumentException("there must be at least one core");
        }
        this.cores = cores;
    }

    protected abstract void stopImpl();

    synchronized void shutdown() {
        if (threadPool != null) {
            threadPool.shutdownNow();
        }
    }

    synchronized boolean isShutDown() {
        if (threadPool != null) {
            return threadPool.isShutdown();
        }
        return false;
    }

    public synchronized void stop() {
        try {
            stopImpl();
        } finally {
            shutdown();
            stopped = true;
        }
    }

    public O execute(I input) {
        try {
            return executeNoShutDown(input);
        } finally {
            shutdown();
        }
    }

    public O executeNoShutDown(I input) {
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
        if (!getThreadPool().isShutdown()) {
            for (Callable<T> callable : callables) {
                futures.add(getThreadPool().submit(callable));
            }
        }
        return futures;
    }

    private synchronized ExecutorService getThreadPool() {
        if (threadPool == null || threadPool.isShutdown()) {
            threadPool = Executors.newFixedThreadPool(cores);
        }
        return threadPool;
    }

    public synchronized boolean isStopped() {
        return stopped || Thread.currentThread().isInterrupted();
    }

    protected int getCores() {
        return cores;
    }

    public UUID getServiceId() {
        return serviceId;
    }
}
