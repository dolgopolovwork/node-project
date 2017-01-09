package ru.babobka.factor.benchmark;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

import ru.babobka.factor.service.EllipticCurveFactorService;

public class Benchmark {

	public static void main(String[] args) {

		//displayStatistics(32, tests, i+1);
		

	}

	public static void displayStatistics(int factorBits, int tests, int cores) {
		long sumTime = 0;
		long timeTakes = 0;
		long[] timeArray = new long[tests];
		long minTime = 0;
		long maxTime = 0;
		long oldTime;
		for (int i = 0; i < tests; i++) {
			BigInteger number = BigInteger.probablePrime(factorBits, new Random())
					.multiply(BigInteger.probablePrime(factorBits, new Random()));
			oldTime = System.currentTimeMillis();

			new EllipticCurveFactorService().factor(number, cores);

			timeTakes = System.currentTimeMillis() - oldTime;
			timeArray[i] = timeTakes;
			sumTime += timeTakes;
			if (i == 0) {
				minTime = timeTakes;
				maxTime = timeTakes;
			} else if (minTime > timeTakes) {
				minTime = timeTakes;
			} else if (maxTime < timeTakes) {
				maxTime = timeTakes;
			}

		}
		Arrays.sort(timeArray);
		System.out.println("bits\t|" + (factorBits * 2) + "\tcores " + cores + "\t|\tavg time " + (sumTime / tests)
				+ "\t|\tmedian time " + timeArray[tests / 2] + "\t|\tmin time " + minTime + "\t|\tmax time " + maxTime);
	}
}
