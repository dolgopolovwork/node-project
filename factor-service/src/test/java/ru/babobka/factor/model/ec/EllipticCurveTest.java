package ru.babobka.factor.model.ec;

import org.junit.Test;
import ru.babobka.nodeutils.math.Zp;

import java.math.BigInteger;

/**
 * Created by 123 on 14.10.2017.
 */
public class EllipticCurveTest {

    @Test(expected = IllegalArgumentException.class)
    public void testNullA() {
        new EllipticCurve(null, Zp.random(BigInteger.TEN), BigInteger.TEN);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullB() {
        new EllipticCurve(Zp.random(BigInteger.TEN), null, BigInteger.TEN);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullMod() {
        new EllipticCurve(Zp.random(BigInteger.TEN), Zp.random(BigInteger.TEN), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLittleMod() {
        new EllipticCurve(Zp.random(BigInteger.TEN), Zp.random(BigInteger.TEN), BigInteger.ONE);
    }
}
