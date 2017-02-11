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

	private static final UUID DUMMY_UUID = new UUID(0, 0);

	public NodeRequest getNumberRequest(BigInteger number) {
		Map<String, Serializable> dataMap = new HashMap<>();
		dataMap.put("number", number);
		return NodeRequest.race(DUMMY_UUID, "ellipticFactor", dataMap);
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

			BigInteger factor = (BigInteger) TASK.newInstance().execute(getNumberRequest(number)).getResultMap()
					.get("factor");
			assertEquals(number.mod(factor), BigInteger.ZERO);

		}
	}
}