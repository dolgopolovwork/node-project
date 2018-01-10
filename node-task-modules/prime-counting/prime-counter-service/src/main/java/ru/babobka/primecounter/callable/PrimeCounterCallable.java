package ru.babobka.primecounter.callable;

import ru.babobka.nodeutils.container.Container;
import ru.babobka.primecounter.model.Range;
import ru.babobka.primecounter.tester.PrimeTester;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by dolgopolov.a on 07.07.15.
 */
public class PrimeCounterCallable implements Callable<Integer> {

    private final PrimeTester tester = Container.getInstance().get(PrimeTester.class);
    private final Range range;

    public PrimeCounterCallable(Range range) {
        if (range == null) {
            throw new IllegalArgumentException("range is null");
        }
        this.range = range;
    }

    public static List<PrimeCounterCallable> createCalls(List<Range> ranges) {
        if (ranges == null) {
            throw new IllegalArgumentException("can not create calls of null ranges");
        }
        List<PrimeCounterCallable> callables = new ArrayList<>(ranges.size());
        for (Range range : ranges) {
            callables.add(new PrimeCounterCallable(range));
        }
        return callables;
    }

    @Override
    public Integer call() {
        int result = 0;
        long counter = range.getBegin();
        while (!Thread.currentThread().isInterrupted() && counter <= range.getEnd()) {
            if (tester.isPrime(counter)) {
                result++;
            }
            counter++;
        }
        return result;
    }
}
