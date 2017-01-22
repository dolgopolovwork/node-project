package ru.babobka.factor;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.Random;

import org.junit.Test;

import ru.babobka.factor.model.EllipticCurveProjective;

public class EllipticCurveTest {

	@Test
	public void testGroupProperties() {
		int bits = 32;
		for (int i = 0; i < 1000; i++) {
			EllipticCurveProjective a = EllipticCurveProjective
					.generateRandomCurve(BigInteger.probablePrime(bits, new Random()));
			assertEquals(a.add(a.add(a)), a.add(a).add(a));
		}
	}

}
