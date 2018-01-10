package ru.babobka.primecounter.service;

import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.thread.ThreadPoolService;
import ru.babobka.primecounter.callable.PrimeCounterCallable;
import ru.babobka.primecounter.model.Range;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by 123 on 20.06.2017.
 */
public class PrimeCounterTaskService extends ThreadPoolService<Range, Integer> {

    private final SimpleLogger logger = Container.getInstance().get(SimpleLogger.class);

    PrimeCounterTaskService(int cores) {
        super(cores);
    }

    @Override
    protected void stopImpl() {
        //do nothing
    }

    @Override
    protected Integer executeImpl(Range range) {
        int result = 0;
        List<Range> ranges = Range.getRanges(range.getBegin(), range.getEnd(), getCores());
        List<Future<Integer>> futureList = submit(PrimeCounterCallable.createCalls(ranges));
        try {
            for (Future<Integer> future : futureList) {
                result += future.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            logger.error(e);
        }
        return result;
    }
}
