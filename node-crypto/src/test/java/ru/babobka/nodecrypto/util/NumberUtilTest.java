package ru.babobka.nodecrypto.util;

import org.junit.Test;
import ru.babobka.nodecrypto.model.SafePrime;

import java.math.BigInteger;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by 123 on 15.08.2017.
 */
public class NumberUtilTest {

    @Test(expected = IllegalArgumentException.class)
    public void testGenerateExponentNullArgument() {
        NumberUtil.generateExponent(null);
    }

    @Test
    public void testGenerateExponent() {
        int bits = 32;
        SafePrime safePrime = SafePrime.randomPrime(bits);
        BigInteger exponent = NumberUtil.generateExponent(safePrime);
        assertNotEquals(exponent, safePrime.getPrime().subtract(BigInteger.ONE));
        assertNotEquals(exponent, BigInteger.ONE);
        assertTrue(exponent.compareTo(safePrime.getPrime()) < 0);
    }
}
