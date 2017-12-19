package ru.babobka.factor.benchmark;

import ru.babobka.factor.model.ec.multprovider.FastMultiplicationProvider;
import ru.babobka.factor.service.EllipticCurveFactorService;
import ru.babobka.factor.service.EllipticCurveFactorServiceFactory;
import ru.babobka.nodeutils.container.ApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

import static org.mockito.Mockito.mock;

public class FactorizingBenchmark {


    public static void main(String[] args) throws InterruptedException {
        new ApplicationContainer() {
            @Override
            public void contain(Container container) {
                container.put(mock(SimpleLogger.class));
                container.put(new FastMultiplicationProvider());
            }
        }.contain(Container.getInstance());

        for (int i = 32; i < 40; i++) {
            displayStatistics(i, 100, 4);
        }
    }

    public static void displayStatistics(int factorBits, int tests, int cores) {
        EllipticCurveFactorService ellipticCurveFactorService = new EllipticCurveFactorServiceFactory().get(cores);
        long sumTime = 0;
        long timeTakes;
        long[] timeArray = new long[tests];
        long minTime = 0;
        long maxTime = 0;
        long oldTime;
        try {
            for (int i = 0; i < tests; i++) {
                BigInteger number = BigInteger.probablePrime(factorBits, new Random())
                        .multiply(BigInteger.probablePrime(factorBits, new Random()));
                oldTime = System.currentTimeMillis();
                ellipticCurveFactorService.executeNoShutDown(number);
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
        } finally {
            ellipticCurveFactorService.stop();
        }
        Arrays.sort(timeArray);
        System.out.println("bits " + (factorBits * 2) + "\t|\tcores " + cores + "\t|\tavg time " + (sumTime / tests)
                + "\t|\tmedian time " + timeArray[tests / 2] + "\t|\tmin time " + minTime + "\t|\tmax time " + maxTime);
    }

    /*
Without fast binary
bits	|64	cores 4	|	avg time 382	|	median time 166	|	min time 9	|	max time 1882
bits	|66	cores 4	|	avg time 324	|	median time 145	|	min time 14	|	max time 1841
bits	|68	cores 4	|	avg time 337	|	median time 306	|	min time 10	|	max time 1191
bits	|70	cores 4	|	avg time 571	|	median time 476	|	min time 19	|	max time 1508
bits	|72	cores 4	|	avg time 573	|	median time 376	|	min time 35	|	max time 2424
bits	|74	cores 4	|	avg time 608	|	median time 495	|	min time 18	|	max time 2410
bits	|76	cores 4	|	avg time 659	|	median time 508	|	min time 11	|	max time 2237
bits	|78	cores 4	|	avg time 621	|	median time 383	|	min time 29	|	max time 2814

After refactor
bits 64	|	cores 4	|	avg time 209	|	median time 92	|	min time 11	|	max time 1708
bits 66	|	cores 4	|	avg time 368	|	median time 175	|	min time 10	|	max time 1877
bits 68	|	cores 4	|	avg time 403	|	median time 170	|	min time 6	|	max time 4453
bits 70	|	cores 4	|	avg time 437	|	median time 235	|	min time 14	|	max time 4343
bits 72	|	cores 4	|	avg time 535	|	median time 343	|	min time 15	|	max time 2317
bits 74	|	cores 4	|	avg time 671	|	median time 520	|	min time 6	|	max time 2628
bits 76	|	cores 4	|	avg time 706	|	median time 463	|	min time 23	|	max time 3273
bits 78	|	cores 4	|	avg time 901	|	median time 749	|	min time 16	|	max time 4058
     */
}
