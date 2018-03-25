package ru.babobka.primecounter.callable;

import ru.babobka.nodeutils.container.Container;
import ru.babobka.primecounter.model.Range;
import ru.babobka.primecounter.tester.PrimeTester;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by dolgopolov.a on 07.07.15.
 */
public class PrimeCounterCallable implements Callable<Integer> {

    private final PrimeTester tester = Container.getInstance().get(PrimeTester.class);
    private final Range range;
    private final AtomicBoolean done;

    public PrimeCounterCallable(AtomicBoolean done, Range range) {
        if (done == null) {
            throw new IllegalArgumentException("done is null");
        } else if (range == null) {
            throw new IllegalArgumentException("range is null");
        }
        this.range = range;
        this.done = done;
    }

    public static List<PrimeCounterCallable> createCalls(AtomicBoolean done, List<Range> ranges) {
        if (ranges == null) {
            throw new IllegalArgumentException("cannot create calls of null ranges");
        }
        List<PrimeCounterCallable> callables = new ArrayList<>(ranges.size());
        for (Range range : ranges) {
            callables.add(new PrimeCounterCallable(done, range));
        }
        return callables;
    }

    @Override
    public Integer call() {
        int result = 0;
        long counter = range.getBegin();
        while (!done.get() && counter <= range.getEnd()) {
            if (tester.isPrime(counter)) {
                result++;
            }
            counter++;
        }
        return result;
    }
}
