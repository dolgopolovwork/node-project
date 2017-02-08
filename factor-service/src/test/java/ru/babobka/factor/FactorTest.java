package ru.babobka.factor;

import static org.junit.Assert.*;

import java.math.BigInteger;
import java.util.Random;

import org.junit.Test;

import ru.babobka.factor.model.FactoringResult;
import ru.babobka.factor.service.EllipticCurveFactorService;

public class FactorTest {

	@Test
	public void testLittleNumbers() {
		int tests = 150;
		int factorBits = 16;
		generateTests(tests, factorBits);
	}

	@Test
	public void testMediumNumbers() {
		int tests = 30;
		int factorBits = 30;
		generateTests(tests, factorBits);
	}

	@Test
	public void testLargeNumbers() {
		int tests = 10;
		int factorBits = 35;
		generateTests(tests, factorBits);
	}

	@Test
	public void testVeryBigNumbers() {
		int tests = 5;
		int factorBits = 45;
		generateTests(tests, factorBits);
	}
	
	@Test
	public void testSuperBigNumber() {
		int tests = 1;
		int factorBits = 55;
		generateTests(tests, factorBits);
	}

	

	private void generateTests(int tests, int factorBits) {
		for (int i = 0; i < tests; i++) {
			BigInteger number = BigInteger.probablePrime(factorBits, new Random())
					.multiply(BigInteger.probablePrime(factorBits, new Random()));
			FactoringResult result = new EllipticCurveFactorService().factor(number);
			assertEquals(number.mod(result.getFactor()), BigInteger.ZERO);
		}
	}
}
