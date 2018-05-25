package ru.babobka.primecounter.service;

import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.NodeLogger;
import ru.babobka.nodeutils.thread.ThreadPoolService;
import ru.babobka.primecounter.callable.PrimeCounterCallable;
import ru.babobka.primecounter.model.Range;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by 123 on 20.06.2017.
 */
public class PrimeCounterTaskService extends ThreadPoolService<Range, Integer> {

    private final NodeLogger nodeLogger = Container.getInstance().get(NodeLogger.class);
    private final AtomicBoolean done = new AtomicBoolean(false);

    PrimeCounterTaskService(int cores) {
        super(cores);
    }

    @Override
    protected void stopImpl() {
        done.set(true);
    }

    @Override
    protected Integer getStoppedResponse() {
        return 0;
    }

    @Override
    protected Integer executeImpl(Range range) {
        int result = 0;
        List<Range> ranges = Range.getRanges(range.getBegin(), range.getEnd(), getCores());
        List<Future<Integer>> futureList = submit(PrimeCounterCallable.createCalls(done, ranges));
        try {
            for (Future<Integer> future : futureList) {
                result += future.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            nodeLogger.error(e);
        }
        return result;
    }
}
