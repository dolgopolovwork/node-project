package ru.babobka.nodeutils.math;

import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.*;

/**
 * Created by 123 on 26.09.2017.
 */
public class FpTest {

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNullNumber() {
        new Fp(null, BigInteger.TEN);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNullMod() {
        new Fp(BigInteger.TEN, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNegativeMod() {
        new Fp(BigInteger.TEN, BigInteger.valueOf(-1L));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorZeroMod() {
        new Fp(BigInteger.TEN, BigInteger.ZERO);
    }

    @Test
    public void testGetNumberNegative() {
        Fp fp = new Fp(BigInteger.valueOf(-1), BigInteger.TEN);
        assertEquals(fp.getNumber(), BigInteger.valueOf(9L));
    }

    @Test
    public void testGetNumber() {
        int number = 22;
        int mod = 10;
        Fp fp = new Fp(BigInteger.valueOf(number), BigInteger.valueOf(mod));
        assertEquals(fp.getNumber(), BigInteger.valueOf(number % mod));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRandomNullMod() {
        Fp.random(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRandomNegativeMod() {
        Fp.random(BigInteger.valueOf(-1L));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRandomToLittleMod() {
        Fp.random(BigInteger.ONE);
    }

    @Test
    public void testRandom() {
        int n = 5;
        Fp[] fps = new Fp[n];
        BigInteger mod = BigInteger.valueOf(100);
        //Pr(all 5 are equal)=(1/100)^5
        for (int i = 0; i < fps.length; i++) {
            fps[i] = Fp.random(mod);
        }
        boolean equal = true;
        for (int i = 1; i < fps.length; i++) {
            if (!fps[0].equals(fps[i])) {
                equal = false;
                break;
            }
        }
        assertFalse(equal);
    }

    @Test
    public void testAddZpNeutral() {
        BigInteger mod = BigInteger.TEN;
        Fp fp = Fp.random(mod);
        assertEquals(fp, fp.add(Fp.addNeutral(mod)));
    }

    @Test
    public void testAdd() {
        BigInteger mod = BigInteger.TEN;
        Fp fp1 = Fp.random(mod);
        Fp fp2 = Fp.random(mod);
        assertEquals(fp1.add(fp2).getNumber(), fp1.getNumber().add(fp2.getNumber()).mod(mod));
    }

    @Test
    public void testAddCommutativity() {
        BigInteger mod = BigInteger.TEN;
        for (int i = 0; i < 100; i++) {
            Fp fp1 = Fp.random(mod);
            Fp fp2 = Fp.random(mod);
            assertEquals(fp1.add(fp2), fp2.add(fp1));
        }
    }

    @Test
    public void testAddAssociativity() {
        BigInteger mod = BigInteger.TEN;
        for (int i = 0; i < 100; i++) {
            Fp fp1 = Fp.random(mod);
            Fp fp2 = Fp.random(mod);
            Fp fp3 = Fp.random(mod);
            assertEquals((fp1.add(fp2)).add(fp3), fp2.add(fp1.add(fp3)));
        }
    }

    @Test
    public void testSubtractZpNeutral() {
        BigInteger mod = BigInteger.TEN;
        Fp fp = Fp.random(mod);
        assertEquals(fp, fp.subtract(Fp.addNeutral(mod)));
    }

    @Test
    public void testSubtract() {
        BigInteger mod = BigInteger.TEN;
        for (int i = 0; i < 100; i++) {
            Fp fp1 = Fp.random(mod);
            Fp fp2 = Fp.random(mod);
            assertEquals(fp1.subtract(fp2).getNumber(), fp1.getNumber().subtract(fp2.getNumber()).mod(mod));
        }
    }

    @Test
    public void testMultNeutral() {
        BigInteger mod = BigInteger.TEN;
        Fp fp = Fp.random(mod);
        assertEquals(fp, fp.mult(Fp.multNeutral(mod)));
    }

    @Test
    public void testMult() {
        BigInteger mod = BigInteger.TEN;
        Fp fp1 = Fp.random(mod);
        Fp fp2 = Fp.random(mod);
        assertEquals(fp1.mult(fp2).getNumber(), fp1.getNumber().multiply(fp2.getNumber()).mod(mod));
    }

    @Test
    public void testMultCommutativity() {
        BigInteger mod = BigInteger.TEN;
        for (int i = 0; i < 100; i++) {
            Fp fp1 = Fp.random(mod);
            Fp fp2 = Fp.random(mod);
            assertEquals(fp1.mult(fp2), fp2.mult(fp1));
        }
    }

    @Test
    public void testMultAssociativity() {
        BigInteger mod = BigInteger.TEN;
        for (int i = 0; i < 100; i++) {
            Fp fp1 = Fp.random(mod);
            Fp fp2 = Fp.random(mod);
            Fp fp3 = Fp.random(mod);
            assertEquals((fp1.mult(fp2)).mult(fp3), fp2.mult(fp1.mult(fp3)));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPowNull() {
        Fp.random(BigInteger.TEN).pow(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPowNegative() {
        Fp.random(BigInteger.TEN).pow(BigInteger.valueOf(-1L));
    }

    @Test
    public void testPowOne() {
        BigInteger mod = BigInteger.TEN;
        Fp fp = Fp.random(mod);
        assertEquals(fp.pow(BigInteger.ONE), fp);
    }

    @Test
    public void testPowNeutral() {
        BigInteger mod = BigInteger.TEN;
        Fp fp = Fp.multNeutral(mod);
        assertEquals(fp.pow(BigInteger.TEN), fp);
    }

    @Test
    public void testPow() {
        BigInteger mod = BigInteger.TEN;
        Fp fp = Fp.random(mod);
        BigInteger exp = BigInteger.TEN;
        assertEquals(fp.pow(exp).getNumber(), fp.getNumber().modPow(exp, mod));
    }

    @Test
    public void testPowLaw() {
        BigInteger mod = BigInteger.TEN;
        BigInteger exp1 = BigInteger.valueOf(4L);
        BigInteger exp2 = BigInteger.valueOf(5L);
        Fp fp = Fp.random(mod);
        assertEquals(fp.pow(exp1).pow(exp2), fp.pow(exp1.multiply(exp2)));
        assertEquals(fp.pow(exp1).mult(fp.pow(exp2)), fp.pow(exp1.add(exp2)));
    }

    @Test
    public void testSquare() {
        BigInteger mod = BigInteger.TEN;
        Fp fp = Fp.random(mod);
        assertEquals(fp.square(), fp.pow(BigInteger.valueOf(2L)));
    }

    @Test
    public void testQube() {
        BigInteger mod = BigInteger.TEN;
        Fp fp = Fp.random(mod);
        assertEquals(fp.qube(), fp.pow(BigInteger.valueOf(3L)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMultBigIntegerNull() {
        Fp.random(BigInteger.TEN).mult((BigInteger) null);
    }

    @Test
    public void testMultBigIntegerNeutral() {
        BigInteger mod = BigInteger.TEN;
        Fp fp = Fp.random(mod);
        assertEquals(fp.mult(BigInteger.ONE), fp);
    }

    @Test
    public void testMultBigInteger() {
        BigInteger mod = BigInteger.TEN;
        BigInteger mult = BigInteger.valueOf(3L);
        Fp fp = Fp.random(mod);
        assertEquals(fp.mult(mult).getNumber(), fp.getNumber().multiply(mult).mod(mod));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDivideZero() {
        BigInteger mod = BigInteger.TEN;
        Fp fp = Fp.random(mod);
        Fp zero = new Fp(BigInteger.ZERO, mod);
        fp.divide(zero);
    }

    @Test
    public void testDivideItself() {
        BigInteger mod = BigInteger.valueOf(11L);
        for (long l = 1; l < mod.longValue(); l++) {
            Fp fp = new Fp(BigInteger.valueOf(l), mod);
            assertTrue(fp.divide(fp).isMultNeutral());
        }
    }

    @Test
    public void testDivide() {
        BigInteger mod = BigInteger.valueOf(11L);
        for (long i = 1; i < mod.longValue(); i++) {
            Fp fp = new Fp(BigInteger.valueOf(i), mod);
            BigInteger bigI = BigInteger.valueOf(i);
            assertEquals(fp.divide(new Fp(bigI, mod)).getNumber(), fp.getNumber().multiply(bigI.modInverse(mod)).mod(mod));
        }
    }

    @Test
    public void testNegate() {
        BigInteger mod = BigInteger.TEN;
        Fp fp = Fp.random(mod);
        assertTrue(fp.add(fp.negate()).isAddNeutral());
    }

}