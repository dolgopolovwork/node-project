package ru.babobka.factor.task;

import static org.junit.Assert.*;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.junit.Test;

import ru.babobka.nodeserials.NodeRequest;

import ru.babobka.subtask.model.SubTask;

public class TaskTest {

	private static final SubTask TASK = new EllipticCurveFactorTask();

	public NodeRequest getNumberRequest(BigInteger number) {
		Map<String, Serializable> additionMap = new HashMap<>();
		additionMap.put("number", number);
		return new NodeRequest(UUID.randomUUID(), UUID.randomUUID(), "ellipticFactor", additionMap, false, false);
	}

	public BigInteger getRandomHalfPrime(int bits) {
		return BigInteger.probablePrime(bits, new Random()).multiply(BigInteger.probablePrime(bits, new Random()));
	}

	@Test
	public void testValidation() {
		BigInteger number = BigInteger.probablePrime(8, new Random())
				.multiply(BigInteger.probablePrime(8, new Random()));
		assertTrue(TASK.validateRequest(getNumberRequest(number)).isValid());
		number = number.negate();
		assertFalse(TASK.validateRequest(getNumberRequest(number)).isValid());
		number = BigInteger.valueOf(15485863L);
		assertFalse(TASK.validateRequest(getNumberRequest(number)).isValid());
		number = BigInteger.probablePrime(64, new Random());
		assertFalse(TASK.validateRequest(getNumberRequest(number)).isValid());
	}

	@Test
	public void testLittleNumber() {

		generateTest(8, 1000);
	}

	@Test
	public void testMediumNumber() {
		generateTest(16, 500);
	}

	@Test
	public void testBigNumber() {
		generateTest(32, 25);
	}

	@Test
	public void testVeryBigNumber() {
		generateTest(40, 10);
	}

	@Test
	public void testExtraBigNumber() {

		generateTest(45, 5);
	}

	public void generateTest(int bits, int tests) {
		for (int i = 0; i < tests; i++) {

			BigInteger number = getRandomHalfPrime(bits);
			System.out.println("Try to find factor " + number);
			Timer timer = new Timer("Factorization");
			BigInteger factor = (BigInteger) TASK.newInstance().execute(getNumberRequest(number)).getResultMap().get("factor");
			assertEquals(number.mod(factor), BigInteger.ZERO);
			System.out.println(timer);

		}
	}

	private static class Timer {
		private final long time;

		private final String title;

		public Timer(String title) {
			this.time = System.currentTimeMillis();
			this.title = title;
		}

		public String toString() {
			return title + " takes " + (System.currentTimeMillis() - time) + "mls";
		}
	}
}