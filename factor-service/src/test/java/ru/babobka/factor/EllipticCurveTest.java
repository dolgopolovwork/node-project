package ru.babobka.factor;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;
import java.util.Random;

import org.junit.Test;

import ru.babobka.factor.model.EllipticCurveProjective;

public class EllipticCurveTest {

    private static final int TESTS = 1000;

    private static final int BITS = 26;

    @Test
    public void testAddAssociative() {
	for (int i = 0; i < TESTS; i++) {
	    EllipticCurveProjective a = EllipticCurveProjective
		    .generateRandomCurve(BigInteger.probablePrime(BITS, new Random()));
	    assertEquals(a.add(a.add(a)), a.add(a).add(a));
	}
    }

    @Test
    public void testMultAssociative() {
	Random random = new Random();
	for (int i = 0; i < TESTS; i++) {
	    int random1 = random.nextInt(100) + 1;
	    int random2 = random.nextInt(100) + 1;
	    EllipticCurveProjective a = EllipticCurveProjective
		    .generateRandomCurve(BigInteger.probablePrime(BITS, new Random()));
	    EllipticCurveProjective b = a.copy();
	    assertEquals(a.multiply(random1).multiply(random2), b.multiply(random2).multiply(random1));
	}

    }

    @Test
    public void testMultLinear() {
	Random random = new Random();
	for (int i = 0; i < TESTS; i++) {
	    EllipticCurveProjective a = EllipticCurveProjective
		    .generateRandomCurve(BigInteger.probablePrime(BITS, new Random()));
	    EllipticCurveProjective b = a.copy();
	    EllipticCurveProjective originalA = a.copy();
	    int mult = random.nextInt(100) + 1;
	    for (int j = 1; j < mult; j++) {
		a = a.add(originalA);
	    }
	    assertEquals(a, b.multiply(mult));
	}
    }

}
