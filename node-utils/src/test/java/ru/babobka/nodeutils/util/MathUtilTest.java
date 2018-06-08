package ru.babobka.nodeutils.util;

import org.junit.Test;
import ru.babobka.nodeutils.thread.ThreadPoolService;

import java.math.BigInteger;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * Created by 123 on 21.10.2017.
 */
public class MathUtilTest {

    private static final int[] PRIMES = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 2791, 6329683, 12799541};

    private static final long[] LONG_PRIMES = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 2791, 6329683, 12799541};

    private static final int[] COMPOSITES = {4, 6, 10, 14, 22, 26, 17 * 3, 19 * 5, 23 * 7, 29 * 11, 31 * 13, 2791 * 2791, 6329683 * 31, 12799541 * 11};

    private static final String[] BIG_PRIMES = {"163", "1307", "2204009", "10153313", "13538879", "67280421310721", "170141183460469231731687303715884105727"};

    @Test
    public void testIsPrime() {
        for (int prime : PRIMES) {
            assertTrue(MathUtil.isPrime(prime));
        }
    }

    @Test
    public void testIsPrimeIntNegative() {
        for (int prime : PRIMES) {
            assertTrue(MathUtil.isPrime(-prime));
        }
    }

    @Test
    public void testIsNotPrime() {
        for (int prime : COMPOSITES) {
            assertFalse(MathUtil.isPrime(prime));
        }
    }

    @Test
    public void testIsNotPrimeNegative() {
        for (int prime : COMPOSITES) {
            assertFalse(MathUtil.isPrime(-prime));
        }
    }

    @Test
    public void testSqrtBig() {
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            BigInteger number = new BigInteger(64, random);
            assertEquals(number, MathUtil.sqrtBig(number.multiply(number)));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCbrtNullNumber() {
        MathUtil.cbrt(null);
    }

    @Test
    public void testCbrt() {
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            BigInteger number = new BigInteger(64, random);
            assertEquals(number, MathUtil.cbrt(number.multiply(number).multiply(number)));
        }
    }

    @Test
    public void testIsPrimeNegative() {
        assertFalse(MathUtil.isPrime(-1));
    }

    @Test
    public void testIsPrimeOne() {
        assertFalse(MathUtil.isPrime(1));
    }

    @Test
    public void testLog() {
        for (int number = 2; number < 10; number++) {
            for (int exp = 2; exp < 5; exp++) {
                assertEquals(exp, MathUtil.log(number, (int) Math.pow(number, exp)));
            }
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDummyFactorNegative() {
        MathUtil.dummyFactor(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDummyFactorZero() {
        MathUtil.dummyFactor(0);
    }

    @Test
    public void testDummyFactorPrime() {
        for (int prime : PRIMES) {
            assertEquals(MathUtil.dummyFactor(prime), 1);
        }
    }

    @Test
    public void testDummyFactor() {
        for (long prime : LONG_PRIMES) {
            assertEquals(MathUtil.dummyFactor(prime * prime), prime);
        }
    }

    @Test
    public void testEea() {
        int max = 500;
        for (int i = 1; i <= max; i++) {
            for (int j = 1; j <= max; j++) {
                BigInteger a = BigInteger.valueOf(i);
                BigInteger b = BigInteger.valueOf(j);
                MathUtil.BigIntEuclidean euclidean = MathUtil.eea(a, b);
                assertEquals(euclidean.getX().multiply(a).add(euclidean.getY().multiply(b)), euclidean.getGcd());
                if (!a.equals(euclidean.getGcd()) && !b.equals(euclidean.getGcd())) {
                    assertEquals(a.multiply(euclidean.getX()).mod(b), euclidean.getGcd());
                }
            }
        }
    }


    @Test(expected = IllegalArgumentException.class)
    public void testGetRelativePrimeNullNumber() {
        MathUtil.getRelativePrime(null);
    }

    @Test
    public void testGetRelativePrime() throws InterruptedException {
        int bits = 45;
        Random random = new Random();
        ExecutorService executorService = ThreadPoolService.createDaemonPool(Runtime.getRuntime().availableProcessors());
        AtomicBoolean failed = new AtomicBoolean();
        AtomicInteger counter = new AtomicInteger();
        int tests = 1000;
        for (int i = 0; i < tests; i++) {
            executorService.submit(() -> {
                byte[] numberBytes = new byte[8 + random.nextInt(bits)];
                random.nextBytes(numberBytes);
                BigInteger number = new BigInteger(numberBytes);
                BigInteger relativePrime = MathUtil.getRelativePrime(number);
                if (!number.gcd(relativePrime).equals(BigInteger.ONE)) {
                    failed.set(true);
                    executorService.shutdownNow();
                } else if (counter.incrementAndGet() == tests) {
                    executorService.shutdownNow();
                }
            });
        }
        executorService.awaitTermination(2, TimeUnit.MINUTES);
        if (failed.get()) {
            fail();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsPrimeBigNullNumber() {
        MathUtil.isPrime(null);
    }

    @Test
    public void testIsPrimeBig() {
        for (String bigPrime : BIG_PRIMES) {
            assertTrue(MathUtil.isPrime(new BigInteger(bigPrime)));
        }
    }

    @Test
    public void testIsNotPrimeBig() {
        for (String bigPrime : BIG_PRIMES) {
            BigInteger number = new BigInteger(bigPrime);
            assertFalse(MathUtil.isPrime(number.multiply(number)));
        }
    }

}
