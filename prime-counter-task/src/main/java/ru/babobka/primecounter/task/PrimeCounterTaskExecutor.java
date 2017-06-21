package ru.babobka.primecounter.task;

import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.primecounter.callable.PrimeCounterCallable;
import ru.babobka.primecounter.model.Range;
import ru.babobka.primecounter.util.MathUtil;
import ru.babobka.subtask.model.ExecutionResult;
import ru.babobka.subtask.model.TaskExecutor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by 123 on 20.06.2017.
 */
public class PrimeCounterTaskExecutor extends TaskExecutor {

    private volatile ThreadPoolExecutor threadPool;

    private volatile boolean stopped;

    @Override
    public ExecutionResult execute(int threads, NodeRequest request) {

        try {
            if (!stopped) {
                Map<String, Serializable> result = new HashMap<>();
                long begin = Long.parseLong(request.getStringDataValue(Params.BEGIN.getValue()));
                long end = Long.parseLong(request.getStringDataValue(Params.END.getValue()));
                int cores = Math.min(Runtime.getRuntime().availableProcessors(), threads);
                try {
                    synchronized (this) {
                        if (!stopped) {
                            threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(cores);
                        }
                    }
                    int primes = countPrimes(threadPool, begin, end);
                    result.put(Params.PRIME_COUNT.getValue(), primes);
                } catch (InterruptedException | ExecutionException e) {
                    if (!stopped) {
                        e.printStackTrace();
                    }
                }
                return new ExecutionResult(stopped, result);
            } else {
                return ExecutionResult.stopped();
            }
        } finally {
            if (threadPool != null)
                threadPool.shutdownNow();

        }

    }

    private int countPrimes(ThreadPoolExecutor threadPool, long rangeBegin, long rangeEnd)
            throws InterruptedException, ExecutionException {
        int result = 0;
        if (threadPool != null) {
            Range[] ranges = MathUtil.getRangeArray(rangeBegin, rangeEnd, threadPool.getMaximumPoolSize());
            List<Future<Integer>> futureList = new ArrayList<>(ranges.length);
            for (Range range : ranges) {
                futureList.add(threadPool.submit(new PrimeCounterCallable(range)));
            }
            for (Future<Integer> future : futureList) {
                result += future.get();
            }
            if (stopped) {
                result = 0;
            }
        }
        return result;

    }

    @Override
    public synchronized void stopCurrentTask() {
        stopped = true;
        if (threadPool != null)
            threadPool.shutdownNow();
    }

}
