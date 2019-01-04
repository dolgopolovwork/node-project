package ru.babobka.factor.benchmark;

import ru.babobka.factor.model.ec.multprovider.BinaryMultiplicationProvider;
import ru.babobka.factor.service.EllipticCurveFactorService;
import ru.babobka.factor.service.EllipticCurveFactorServiceFactory;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.key.UtilKey;
import ru.babobka.nodeutils.logger.NodeLogger;
import ru.babobka.nodeutils.thread.ThreadPoolService;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

import static org.mockito.Mockito.mock;

public class DummyFactorizingBenchmark {
    static {
        Container.getInstance().put(container -> {
            container.put(UtilKey.SERVICE_THREAD_POOL, ThreadPoolService.createDaemonPool(Runtime.getRuntime().availableProcessors()));
            container.put(mock(NodeLogger.class));
            container.put(new BinaryMultiplicationProvider());
        });
    }

    public static void main(String[] args) throws InterruptedException {
        for (int i = 40; i < 43; i++) {
            displayStatistics(i, 500);
        }
    }

    private static void displayStatistics(int factorBits, int tests) {
        EllipticCurveFactorService ellipticCurveFactorService = new EllipticCurveFactorServiceFactory().get();
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
                ellipticCurveFactorService.execute(number);
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
        System.out.println("bits " + (factorBits * 2) + "\t|\tavg time " + (sumTime / tests)
                + "\t|\tmedian time " + timeArray[tests / 2] + "\t|\tmin time " + minTime + "\t|\tmax time " + maxTime);
    }

/*
Without fast binary
bits 64|	avg time 382	|	median time 166	|	min time 9	|	max time 1882
bits 66|	avg time 324	|	median time 145	|	min time 14	|	max time 1841
bits 68|	avg time 337	|	median time 306	|	min time 10	|	max time 1191
bits 70|	avg time 571	|	median time 476	|	min time 19	|	max time 1508
bits 72|	avg time 573	|	median time 376	|	min time 35	|	max time 2424
bits 74|	avg time 608	|	median time 495	|	min time 18	|	max time 2410
bits 76|	avg time 659	|	median time 508	|	min time 11	|	max time 2237
bits 78|	avg time 621	|	median time 383	|	min time 29	|	max time 2814

After refactor
bits 64	|	avg time 201	|	median time 98	|	min time 2	|	max time 1513
bits 66	|	avg time 322	|	median time 171	|	min time 4	|	max time 2597
bits 68	|	avg time 340	|	median time 177	|	min time 4	|	max time 4012
bits 70	|	avg time 434	|	median time 270	|	min time 6	|	max time 3411
bits 72	|	avg time 534	|	median time 371	|	min time 8	|	max time 3335
bits 74	|	avg time 547	|	median time 377	|	min time 7	|	max time 4246
bits 76	|	avg time 737	|	median time 534	|	min time 6	|	max time 6192
bits 78	|	avg time 816	|	median time 587	|	min time 6	|	max time 6689
bits 80	|	avg time 1083	|	median time 735	|	min time 14	|	max time 6623
bits 82	|	avg time 1229	|	median time 812	|	min time 26	|	max time 10136
bits 84	|	avg time 1596	|	median time 1077	|	min time 17	|	max time 13942

Both ternary and binary
bits 64	|	avg time 213	|	median time 113	|	min time 4	|	max time 1559
bits 66	|	avg time 295	|	median time 156	|	min time 5	|	max time 2273
bits 68	|	avg time 386	|	median time 235	|	min time 5	|	max time 2815
bits 70	|	avg time 386	|	median time 227	|	min time 10	|	max time 2531
bits 72	|	avg time 449	|	median time 239	|	min time 6	|	max time 3029
bits 74	|	avg time 600	|	median time 484	|	min time 10	|	max time 2866
bits 76	|	avg time 660	|	median time 507	|	min time 7	|	max time 5080
bits 78	|	avg time 820	|	median time 558	|	min time 11	|	max time 5097


Ternary only
bits 64	|	avg time 195	|	median time 103	|	min time 3	|	max time 2004
bits 66	|	avg time 285	|	median time 150	|	min time 4	|	max time 1949
bits 68	|	avg time 315	|	median time 169	|	min time 5	|	max time 2059
bits 70	|	avg time 384	|	median time 224	|	min time 5	|	max time 2540
bits 72	|	avg time 463	|	median time 311	|	min time 5	|	max time 4077
bits 74	|	avg time 586	|	median time 461	|	min time 9	|	max time 4500
bits 76	|	avg time 687	|	median time 476	|	min time 9	|	max time 4381
bits 78	|	avg time 776	|	median time 513	|	min time 8	|	max time 5289
bits 80	|	avg time 1033	|	median time 687	|	min time 19	|	max time 5691
bits 82	|	avg time 1207	|	median time 836	|	min time 15	|	max time 6934
bits 84	|	avg time 1394	|	median time 946	|	min time 13	|	max time 7861
     */
}
