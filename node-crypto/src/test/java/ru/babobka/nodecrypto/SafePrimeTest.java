package ru.babobka.nodecrypto;

import org.junit.Test;

import java.math.BigInteger;
import java.util.Random;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by 123 on 24.04.2017.
 */
public class SafePrimeTest {

    private static final int TESTS = 100;

    @Test
    public void testPrimality() {
        Random random = new Random();
        int bits;
        final int maxBits = 100;
        final int certainty = 50;
        for (int i = 0; i < TESTS; i++) {
            bits = random.nextInt(maxBits) + 3;
            SafePrime safePrime = SafePrime.randomPrime(bits);
            assertTrue(safePrime.getPrime().isProbablePrime(certainty));
            assertTrue(safePrime.getSophieNumber().isProbablePrime(certainty));
        }
    }

    @Test
    public void testNegativeBitsPrime() {
        try {
            SafePrime.randomPrime(-1);
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testTwoBitsPrime() {
        try {
            SafePrime.randomPrime(2);
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

}
