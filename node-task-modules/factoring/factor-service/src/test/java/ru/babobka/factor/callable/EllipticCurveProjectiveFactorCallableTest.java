package ru.babobka.factor.callable;

import org.junit.Test;
import ru.babobka.factor.model.FactoringResult;
import ru.babobka.nodeutils.util.MathUtil;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by 123 on 05.01.2018.
 */
public class EllipticCurveProjectiveFactorCallableTest {

    @Test(expected = IllegalArgumentException.class)
    public void testNullDone() {
        new EllipticCurveProjectiveFactorCallable(null, BigInteger.TEN);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullNumber() {
        new EllipticCurveProjectiveFactorCallable(new AtomicBoolean(), null);
    }

    @Test
    public void testInitPrimes() {
        EllipticCurveProjectiveFactorCallable.initPrimes(100);
        assertFalse(EllipticCurveProjectiveFactorCallable.getPrimes().isEmpty());
        for (Long prime : EllipticCurveProjectiveFactorCallable.getPrimes()) {
            assertTrue(MathUtil.isPrime(prime));
        }
        Set<Long> uniqueNumbers = new HashSet<>(EllipticCurveProjectiveFactorCallable.getPrimes());
        assertEquals(uniqueNumbers.size(), EllipticCurveProjectiveFactorCallable.getPrimes().size());
    }

    @Test
    public void testInitPrimesTwice() {
        EllipticCurveProjectiveFactorCallable.initPrimes(100);
        EllipticCurveProjectiveFactorCallable.initPrimes(100);
        assertFalse(EllipticCurveProjectiveFactorCallable.getPrimes().isEmpty());
        for (Long prime : EllipticCurveProjectiveFactorCallable.getPrimes()) {
            assertTrue(MathUtil.isPrime(prime));
        }
        Set<Long> uniqueNumbers = new HashSet<>(EllipticCurveProjectiveFactorCallable.getPrimes());
        assertEquals(uniqueNumbers.size(), EllipticCurveProjectiveFactorCallable.getPrimes().size());
    }

    @Test
    public void testInitPrimesTwiceLowerBorder() {
        EllipticCurveProjectiveFactorCallable.initPrimes(100);
        int oldSize = EllipticCurveProjectiveFactorCallable.getPrimes().size();
        EllipticCurveProjectiveFactorCallable.initPrimes(50);
        int newSize = EllipticCurveProjectiveFactorCallable.getPrimes().size();
        assertEquals(oldSize, newSize);
    }

    @Test
    public void testInitPrimesTwiceHigherBorder() {
        EllipticCurveProjectiveFactorCallable.clearPrimes();
        EllipticCurveProjectiveFactorCallable.initPrimes(100);
        int oldSize = EllipticCurveProjectiveFactorCallable.getPrimes().size();
        EllipticCurveProjectiveFactorCallable.initPrimes(150);
        int newSize = EllipticCurveProjectiveFactorCallable.getPrimes().size();
        assertNotEquals(oldSize, newSize);
        Set<Long> uniqueNumbers = new HashSet<>(EllipticCurveProjectiveFactorCallable.getPrimes());
        assertEquals(uniqueNumbers.size(), EllipticCurveProjectiveFactorCallable.getPrimes().size());
    }

    @Test
    public void testCallNullResult() {
        EllipticCurveProjectiveFactorCallable callable = spy(new EllipticCurveProjectiveFactorCallable(new AtomicBoolean(), BigInteger.TEN));
        doReturn(null).when(callable).factor();
        assertNull(callable.call());
    }

    @Test
    public void testCall() {
        FactoringResult factoringResult = mock(FactoringResult.class);
        EllipticCurveProjectiveFactorCallable callable = spy(new EllipticCurveProjectiveFactorCallable(new AtomicBoolean(), BigInteger.TEN));
        doReturn(factoringResult).when(callable).factor();
        assertEquals(callable.call(), factoringResult);
    }

    @Test
    public void testCallSecondTry() {
        FactoringResult factoringResult = mock(FactoringResult.class);
        EllipticCurveProjectiveFactorCallable callable = spy(new EllipticCurveProjectiveFactorCallable(new AtomicBoolean(), BigInteger.TEN));
        doReturn(null).doReturn(factoringResult).when(callable).factor();
        assertEquals(callable.call(), factoringResult);
    }
}
