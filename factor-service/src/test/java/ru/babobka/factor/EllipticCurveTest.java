package ru.babobka.factor;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.Random;

import org.junit.Test;

import ru.babobka.factor.model.EllipticCurveProjective;

public class EllipticCurveTest {

	@Test
	public void testMult()  {

		int tests = 1000;
		Random randomBits = new Random();
		Random randomMult = new Random();
		for (int i = 0; i < tests; i++) {
			int bits = randomBits.nextInt(98) +2;
			EllipticCurveProjective curve1 = EllipticCurveProjective
					.generateRandomCurve(BigInteger.probablePrime(bits, new Random()));
			EllipticCurveProjective curve2 = curve1.copy();
			int mult = randomMult.nextInt(65535) + 1;
			assertEquals(curve1.multiply(mult), curve2.oldMultiply(mult));
		}
	}

}
