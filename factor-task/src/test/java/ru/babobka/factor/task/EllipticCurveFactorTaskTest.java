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
import ru.babobka.subtask.model.ValidationResult;

public class EllipticCurveFactorTaskTest {

    private static final String NUMBER = "number";

    private static final SubTask TASK = new EllipticCurveFactorTask();

    private static final UUID DUMMY_UUID = new UUID(0, 0);


    @Test
    public void testIsRequestDataTooSmallLittleNumber() {
        Map<String, Serializable> data = new HashMap<>();
        data.put(NUMBER, "123");
        NodeRequest request = NodeRequest.regular(UUID.randomUUID(), "test", data);
        assertTrue(TASK.isRequestDataTooSmall(request));
    }

    @Test
    public void testIsRequestDataTooSmallBigNumber() {
        Map<String, Serializable> data = new HashMap<>();
        data.put(NUMBER, BigInteger.probablePrime(100, new Random()));
        NodeRequest request = NodeRequest.regular(UUID.randomUUID(), "test", data);
        assertFalse(TASK.isRequestDataTooSmall(request));
    }

    @Test
    public void testValidateRequestNull() {
        ValidationResult validationResult = TASK.validateRequest(null);
        assertFalse(validationResult.isValid());
    }

    @Test
    public void testValidateRequestPrime() {
        Map<String, Serializable> data = new HashMap<>();
        data.put(NUMBER, BigInteger.probablePrime(32, new Random()));
        NodeRequest request = NodeRequest.regular(UUID.randomUUID(), "test", data);
        ValidationResult validationResult = TASK.validateRequest(request);
        assertFalse(validationResult.isValid());
    }


    @Test
    public void testValidateRequestThree() {
        Map<String, Serializable> data = new HashMap<>();
        data.put(NUMBER, BigInteger.valueOf(3));
        NodeRequest request = NodeRequest.regular(UUID.randomUUID(), "test", data);
        ValidationResult validationResult = TASK.validateRequest(request);
        assertFalse(validationResult.isValid());
    }

    @Test
    public void testValidateRequestOk() {
        Map<String, Serializable> data = new HashMap<>();
        data.put(NUMBER, BigInteger.valueOf(1024));
        NodeRequest request = NodeRequest.regular(UUID.randomUUID(), "test", data);
        ValidationResult validationResult = TASK.validateRequest(request);
        assertTrue(validationResult.isValid());
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

    private void generateTest(int bits, int tests) {
        for (int i = 0; i < tests; i++) {

            BigInteger number = getRandomHalfPrime(bits);

            BigInteger factor = (BigInteger) TASK.newInstance().execute(getNumberRequest(number)).getResultMap()
                    .get("factor");
            assertEquals(number.mod(factor), BigInteger.ZERO);

        }
    }

    private NodeRequest getNumberRequest(BigInteger number) {
        Map<String, Serializable> dataMap = new HashMap<>();
        dataMap.put("number", number);
        return NodeRequest.race(DUMMY_UUID, "ellipticFactor", dataMap);
    }

    private BigInteger getRandomHalfPrime(int bits) {
        return BigInteger.probablePrime(bits, new Random()).multiply(BigInteger.probablePrime(bits, new Random()));
    }
}