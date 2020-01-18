package ru.babobka.primecounter.service;

import org.apache.log4j.Logger;
import ru.babobka.nodeutils.thread.ThreadPoolService;
import ru.babobka.primecounter.callable.PrimeCounterCallable;
import ru.babobka.primecounter.model.Range;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by 123 on 20.06.2017.
 */
public class PrimeCounterTaskService extends ThreadPoolService<Range, Integer> {

    private static final Logger logger = Logger.getLogger(PrimeCounterTaskService.class);
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
        List<Future<Integer>> futureList;
        if (range.getEnd() - range.getBegin() < 65536) {
            futureList = submit(PrimeCounterCallable.createCalls(done, Collections.singletonList(range)));
        } else {
            List<Range> ranges = Range.getRanges(range.getBegin(), range.getEnd(), getCores());
            futureList = submit(PrimeCounterCallable.createCalls(done, ranges));
        }
        try {
            for (Future<Integer> future : futureList) {
                result += future.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            logger.error("exception thrown", e);
        }
        return result;
    }
}
