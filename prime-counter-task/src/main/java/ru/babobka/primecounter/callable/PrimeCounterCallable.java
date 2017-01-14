package ru.babobka.primecounter.callable;

import ru.babobka.primecounter.model.Range;
import ru.babobka.primecounter.tester.DummyPrimeTester;
import ru.babobka.primecounter.tester.PrimeTester;

import java.util.concurrent.Callable;

/**
 * Created by dolgopolov.a on 07.07.15.
 */
public class PrimeCounterCallable implements Callable<Integer> {

	private final Range range;


	private static final PrimeTester tester = new DummyPrimeTester();

	public PrimeCounterCallable(Range range) {
		this.range = range;
	}

	@Override
	public Integer call() throws Exception {

		int result = 0;
		long counter = range.getBegin();

		while (!Thread.currentThread().isInterrupted() && counter != range.getEnd() + 1) {
			if (tester.isPrime(counter)) {
				result++;
			}
			counter++;
		}
		return result;
	}

}
