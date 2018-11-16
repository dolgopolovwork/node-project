package ru.babobka.factor.benchmark;

import ru.babobka.factor.model.ec.EllipticCurvePoint;
import ru.babobka.factor.model.ec.multprovider.BinaryMultiplicationProvider;
import ru.babobka.factor.model.ec.multprovider.FastMultiplicationProvider;
import ru.babobka.factor.model.ec.multprovider.TernaryMultiplicationProvider;
import ru.babobka.nodeutils.container.Container;

import java.math.BigInteger;
import java.util.Random;

/**
 * Created by 123 on 26.10.2018.
 */
public class DummyMultiplicationProviderBenchmark {

    private static final int TESTS = 100_000;

    static {
        Container.getInstance().put(
                container -> {
                    container.put(new BinaryMultiplicationProvider());
                    container.put(new TernaryMultiplicationProvider());
                    container.put(new FastMultiplicationProvider());
                }
        );
    }

    private static void warmUp() {
        benchmark(BigInteger.valueOf(1000), 2, 1000);
        System.out.println("Done warm up");
    }

    private static long bigCurveSmallCoefficients() {
        System.out.println("bigCurveSmallCoefficients");
        return benchmark(BigInteger.valueOf(100_000_000), 2, 1000);
    }

    private static long bigCurveMediumCoefficients() {
        System.out.println("bigCurveMediumCoefficients");
        return benchmark(BigInteger.valueOf(100_000_000), 100_000, 1000_000);
    }

    private static long bigCurveBigCoefficients() {
        System.out.println("bigCurveBigCoefficients");
        return benchmark(BigInteger.valueOf(100_000_000), 1_000_000, 10_000_000);
    }

    private static long benchmark(BigInteger mod, int minCoefficient, int maxCoefficient) {
        int delta = maxCoefficient - minCoefficient;
        Random random = new Random();
        long totalTime = 0;
        long startTime;
        int coefficient;
        for (int i = 0; i < TESTS; i++) {
            coefficient = minCoefficient + random.nextInt(delta);
            EllipticCurvePoint ellipticCurvePoint = EllipticCurvePoint.generateRandomPoint(mod);
            startTime = System.currentTimeMillis();
            ellipticCurvePoint.mult(coefficient);
            totalTime += (System.currentTimeMillis()) - startTime;
        }
        return totalTime;
    }

    public static void main(String[] args) {
        warmUp();
        System.out.println(bigCurveSmallCoefficients());
        System.out.println(bigCurveMediumCoefficients());
        System.out.println(bigCurveBigCoefficients());
    }

}
