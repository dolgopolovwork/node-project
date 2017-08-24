package ru.babobka.nodeutils.math;

import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.*;

/**
 * Created by 123 on 26.09.2017.
 */
public class ZpTest {

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNullNumber() {
        new Zp(null, BigInteger.TEN);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNullMod() {
        new Zp(BigInteger.TEN, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNegativeMod() {
        new Zp(BigInteger.TEN, BigInteger.valueOf(-1L));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorZeroMod() {
        new Zp(BigInteger.TEN, BigInteger.ZERO);
    }

    @Test
    public void testGetNumberNegative() {
        Zp zp = new Zp(BigInteger.valueOf(-1), BigInteger.TEN);
        assertEquals(zp.getNumber(), BigInteger.valueOf(9L));
    }

    @Test
    public void testGetNumber() {
        int number = 22;
        int mod = 10;
        Zp zp = new Zp(BigInteger.valueOf(number), BigInteger.valueOf(mod));
        assertEquals(zp.getNumber(), BigInteger.valueOf(number % mod));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRandomNullMod() {
        Zp.random(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRandomNegativeMod() {
        Zp.random(BigInteger.valueOf(-1L));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRandomToLittleMod() {
        Zp.random(BigInteger.ONE);
    }

    @Test
    public void testRandom() {
        int n = 5;
        Zp[] zps = new Zp[n];
        BigInteger mod = BigInteger.valueOf(100);
        //Pr(all 5 are equal)=(1/100)^5
        for (int i = 0; i < zps.length; i++) {
            zps[i] = Zp.random(mod);
        }
        boolean equal = true;
        for (int i = 1; i < zps.length; i++) {
            if (!zps[0].equals(zps[i])) {
                equal = false;
                break;
            }
        }
        assertFalse(equal);
    }

    @Test
    public void testAddZpNeutral() {
        BigInteger mod = BigInteger.TEN;
        Zp zp = Zp.random(mod);
        assertEquals(zp, zp.add(Zp.addNeutral(mod)));
    }

    @Test
    public void testAdd() {
        BigInteger mod = BigInteger.TEN;
        Zp zp1 = Zp.random(mod);
        Zp zp2 = Zp.random(mod);
        assertEquals(zp1.add(zp2).getNumber(), zp1.getNumber().add(zp2.getNumber()).mod(mod));
    }

    @Test
    public void testAddCommutativity() {
        BigInteger mod = BigInteger.TEN;
        for (int i = 0; i < 100; i++) {
            Zp zp1 = Zp.random(mod);
            Zp zp2 = Zp.random(mod);
            assertEquals(zp1.add(zp2), zp2.add(zp1));
        }
    }

    @Test
    public void testAddAssociativity() {
        BigInteger mod = BigInteger.TEN;
        for (int i = 0; i < 100; i++) {
            Zp zp1 = Zp.random(mod);
            Zp zp2 = Zp.random(mod);
            Zp zp3 = Zp.random(mod);
            assertEquals((zp1.add(zp2)).add(zp3), zp2.add(zp1.add(zp3)));
        }
    }

    @Test
    public void testSubtractZpNeutral() {
        BigInteger mod = BigInteger.TEN;
        Zp zp = Zp.random(mod);
        assertEquals(zp, zp.subtract(Zp.addNeutral(mod)));
    }

    @Test
    public void testSubtract() {
        BigInteger mod = BigInteger.TEN;
        for (int i = 0; i < 100; i++) {
            Zp zp1 = Zp.random(mod);
            Zp zp2 = Zp.random(mod);
            assertEquals(zp1.subtract(zp2).getNumber(), zp1.getNumber().subtract(zp2.getNumber()).mod(mod));
        }
    }

    @Test
    public void testMultNeutral() {
        BigInteger mod = BigInteger.TEN;
        Zp zp = Zp.random(mod);
        assertEquals(zp, zp.mult(Zp.multNeutral(mod)));
    }

    @Test
    public void testMult() {
        BigInteger mod = BigInteger.TEN;
        Zp zp1 = Zp.random(mod);
        Zp zp2 = Zp.random(mod);
        assertEquals(zp1.mult(zp2).getNumber(), zp1.getNumber().multiply(zp2.getNumber()).mod(mod));
    }

    @Test
    public void testMultCommutativity() {
        BigInteger mod = BigInteger.TEN;
        for (int i = 0; i < 100; i++) {
            Zp zp1 = Zp.random(mod);
            Zp zp2 = Zp.random(mod);
            assertEquals(zp1.mult(zp2), zp2.mult(zp1));
        }
    }

    @Test
    public void testMultAssociativity() {
        BigInteger mod = BigInteger.TEN;
        for (int i = 0; i < 100; i++) {
            Zp zp1 = Zp.random(mod);
            Zp zp2 = Zp.random(mod);
            Zp zp3 = Zp.random(mod);
            assertEquals((zp1.mult(zp2)).mult(zp3), zp2.mult(zp1.mult(zp3)));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPowNull() {
        Zp.random(BigInteger.TEN).pow(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPowNegative() {
        Zp.random(BigInteger.TEN).pow(BigInteger.valueOf(-1L));
    }

    @Test
    public void testPowOne() {
        BigInteger mod = BigInteger.TEN;
        Zp zp = Zp.random(mod);
        assertEquals(zp.pow(BigInteger.ONE), zp);
    }

    @Test
    public void testPowNeutral() {
        BigInteger mod = BigInteger.TEN;
        Zp zp = Zp.multNeutral(mod);
        assertEquals(zp.pow(BigInteger.TEN), zp);
    }

    @Test
    public void testPow() {
        BigInteger mod = BigInteger.TEN;
        Zp zp = Zp.random(mod);
        BigInteger exp = BigInteger.TEN;
        assertEquals(zp.pow(exp).getNumber(), zp.getNumber().modPow(exp, mod));
    }

    @Test
    public void testPowLaw() {
        BigInteger mod = BigInteger.TEN;
        BigInteger exp1 = BigInteger.valueOf(4L);
        BigInteger exp2 = BigInteger.valueOf(5L);
        Zp zp = Zp.random(mod);
        assertEquals(zp.pow(exp1).pow(exp2), zp.pow(exp1.multiply(exp2)));
        assertEquals(zp.pow(exp1).mult(zp.pow(exp2)), zp.pow(exp1.add(exp2)));
    }

    @Test
    public void testSquare() {
        BigInteger mod = BigInteger.TEN;
        Zp zp = Zp.random(mod);
        assertEquals(zp.square(), zp.pow(BigInteger.valueOf(2L)));
    }

    @Test
    public void testQube() {
        BigInteger mod = BigInteger.TEN;
        Zp zp = Zp.random(mod);
        assertEquals(zp.qube(), zp.pow(BigInteger.valueOf(3L)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMultBigIntegerNull() {
        Zp.random(BigInteger.TEN).mult((BigInteger) null);
    }

    @Test
    public void testMultBigIntegerNeutral() {
        BigInteger mod = BigInteger.TEN;
        Zp zp = Zp.random(mod);
        assertEquals(zp.mult(BigInteger.ONE), zp);
    }

    @Test
    public void testMultBigInteger() {
        BigInteger mod = BigInteger.TEN;
        BigInteger mult = BigInteger.valueOf(3L);
        Zp zp = Zp.random(mod);
        assertEquals(zp.mult(mult).getNumber(), zp.getNumber().multiply(mult).mod(mod));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDivideZero() {
        BigInteger mod = BigInteger.TEN;
        Zp zp = Zp.random(mod);
        Zp zero = new Zp(BigInteger.ZERO, mod);
        zp.divide(zero);
    }

    @Test
    public void testDivideItself() {
        BigInteger mod = BigInteger.valueOf(11L);
        for (long l = 1; l < mod.longValue(); l++) {
            Zp zp = new Zp(BigInteger.valueOf(l), mod);
            assertTrue(zp.divide(zp).isMultNeutral());
        }
    }

    @Test
    public void testDivide() {
        BigInteger mod = BigInteger.valueOf(11L);
        for (long i = 1; i < mod.longValue(); i++) {
            Zp zp = new Zp(BigInteger.valueOf(i), mod);
            BigInteger bigI = BigInteger.valueOf(i);
            assertEquals(zp.divide(new Zp(bigI, mod)).getNumber(), zp.getNumber().multiply(bigI.modInverse(mod)).mod(mod));
        }
    }

    @Test
    public void testNegate() {
        BigInteger mod = BigInteger.TEN;
        Zp zp = Zp.random(mod);
        assertTrue(zp.add(zp.negate()).isAddNeutral());
    }

}