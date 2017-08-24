package ru.babobka.nodeutils.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by 123 on 21.10.2017.
 */
public class MathUtilTest {

    private static final int[] PRIMES = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31};

    @Test
    public void testIsPrime() {
        for (int prime : PRIMES) {
            assertTrue(MathUtil.isPrime(prime));
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
    public void testIsPrimeComposite() {
        for (int prime : PRIMES) {
            assertFalse(MathUtil.isPrime(prime * prime));
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
        for (int prime : PRIMES) {
            assertEquals(MathUtil.dummyFactor(prime * prime), prime);
        }
    }


}
