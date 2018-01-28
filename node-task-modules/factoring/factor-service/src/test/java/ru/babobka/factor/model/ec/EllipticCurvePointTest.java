package ru.babobka.factor.model.ec;

import org.junit.BeforeClass;
import org.junit.Test;
import ru.babobka.factor.model.ec.multprovider.FastMultiplicationProvider;
import ru.babobka.nodeutils.container.Container;

import java.math.BigInteger;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EllipticCurvePointTest {

    private static final int TESTS = 1000;
    private static final int BITS = 26;

    @BeforeClass
    public static void setUp() {
        Container.getInstance().put(new FastMultiplicationProvider());
    }

    @Test
    public void testAddAssociative() {
        for (int i = 0; i < TESTS; i++) {
            EllipticCurvePoint point = EllipticCurvePoint
                    .generateRandomPoint(BigInteger.probablePrime(BITS, new Random()));
            assertEquals(point.add(point.add(point)), point.add(point).add(point));
        }
    }

    @Test
    public void testMultAssociative() {
        Random random = new Random();
        for (int i = 0; i < TESTS; i++) {
            int random1 = random.nextInt(100) + 1;
            int random2 = random.nextInt(100) + 1;
            EllipticCurvePoint pointA = EllipticCurvePoint
                    .generateRandomPoint(BigInteger.probablePrime(BITS, new Random()));
            EllipticCurvePoint pointB = pointA.copy();
            assertEquals(pointA.mult(random1).mult(random2), pointB.mult(random2).mult(random1));
        }
    }

    @Test
    public void testAddNeutral() {
        EllipticCurvePoint point = EllipticCurvePoint
                .generateRandomPoint(BigInteger.probablePrime(BITS, new Random()));
        EllipticCurvePoint neutral = point.getInfinityPoint();
        assertEquals(point, point.add(neutral));
    }

    @Test
    public void testMultNeutral() {
        EllipticCurvePoint point = EllipticCurvePoint
                .generateRandomPoint(BigInteger.probablePrime(BITS, new Random()));
        EllipticCurvePoint neutral = point.getInfinityPoint();
        assertEquals(neutral, neutral.mult(10));
    }

    @Test
    public void testNegate() {
        EllipticCurvePoint pointA = EllipticCurvePoint
                .generateRandomPoint(BigInteger.probablePrime(BITS, new Random()));
        assertTrue(pointA.add(pointA.negate()).isInfinityPoint());
    }

    @Test
    public void testNegateTwoPoints() {
        BigInteger mod = BigInteger.probablePrime(BITS, new Random());
        EllipticCurvePoint pointA = EllipticCurvePoint.generateRandomPoint(mod);
        EllipticCurvePoint pointB = EllipticCurvePoint.generateRandomPoint(mod);
        assertTrue(pointA.add(pointB).add(pointA.negate()).add(pointB.negate()).isInfinityPoint());
    }
}
