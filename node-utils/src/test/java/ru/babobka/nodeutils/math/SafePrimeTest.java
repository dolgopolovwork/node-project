package ru.babobka.nodeutils.math;

import org.junit.Test;
import ru.babobka.nodeutils.util.MathUtil;

import java.math.BigInteger;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * Created by 123 on 02.06.2018.
 */
public class SafePrimeTest {

    private final Random random = new Random();

    @Test(expected = IllegalArgumentException.class)
    public void testNullSophieNumber() {
        new SafePrime(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNotPrime() {
        new SafePrime(BigInteger.TEN);
    }

    @Test
    public void testNotSophiePrime() {
        for (int i = 0; i < 100; i++) {
            try {
                new SafePrime(createNonSophieNumber());
                fail();
            } catch (IllegalArgumentException expected) {
                //it's ok
            }
        }
    }

    @Test
    public void testRandom() {
        for (int i = 0; i < 100; i++) {
            int bits = 8 + random.nextInt(32);
            SafePrime safePrime = SafePrime.random(bits);
            assertTrue(MathUtil.isPrime(safePrime.getPrime()));
            assertTrue(MathUtil.isPrime(safePrime.getSophieNumber()));
            assertEquals(safePrime.getSophieNumber().multiply(BigInteger.valueOf(2)).add(BigInteger.ONE), safePrime.getPrime());
            assertTrue(safePrime.getPrime().bitLength() >= bits);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRandomInvalidBitLength() {
        SafePrime.random(1);
    }

    private BigInteger createNonSophieNumber() {
        int bits = 8 + random.nextInt(32);
        BigInteger prime = BigInteger.probablePrime(bits, random);
        if (MathUtil.isPrime(prime.multiply(BigInteger.valueOf(2)).add(BigInteger.ONE))) {
            return createNonSophieNumber();
        }
        return prime;
    }


}
