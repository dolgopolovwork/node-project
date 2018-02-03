package ru.babobka.nodeclient.service.pollard.parallel;

import org.junit.Test;
import ru.babobka.dlp.service.pollard.parallel.PrimeDistinguishable;
import ru.babobka.nodeutils.math.Fp;

import java.math.BigInteger;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by 123 on 27.01.2018.
 */
public class PrimeDistinguishableTest {
    private PrimeDistinguishable primeDistinguishable = new PrimeDistinguishable();

    @Test
    public void testIsDistinguishableImpl() {
        Fp point = new Fp(BigInteger.valueOf(123456002L), BigInteger.valueOf(Long.MAX_VALUE));
        assertTrue(primeDistinguishable.isDistinguishableImpl(point));
    }

    @Test
    public void testIsNotDistinguishableImpl() {
        Fp point = new Fp(BigInteger.valueOf(123456004L), BigInteger.valueOf(Long.MAX_VALUE));
        assertFalse(primeDistinguishable.isDistinguishableImpl(point));
    }
}
