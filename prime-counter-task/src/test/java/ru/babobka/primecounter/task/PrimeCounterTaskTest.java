package ru.babobka.primecounter.task;

import static org.junit.Assert.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.primecounter.task.PrimeCounterTask;
import ru.babobka.subtask.model.SubTask;
import ru.babobka.subtask.model.ValidationResult;

public class PrimeCounterTaskTest {

    private SubTask task;

    private NodeRequest tenPrimesRequest;

    private NodeRequest thousandPrimesRequest;

    private NodeRequest tenThousandPrimesRequest;

    private NodeRequest millionPrimesRequest;

    private static final UUID DUMMY_UUID = new UUID(0, 0);

    private static final String BEGIN = "begin";

    private static final String END = "end";

    private NodeRequest createRequest(long begin, long end) {
        Map<String, Serializable> dataMap = new HashMap<>();
        dataMap.put("begin", begin);
        dataMap.put("end", end);
        return NodeRequest.regular(DUMMY_UUID, "millerPrimeCounter", dataMap);

    }

    @Before
    public void init() {
        task = new PrimeCounterTask();
        millionPrimesRequest = createRequest(0, 15_485_863);
        thousandPrimesRequest = createRequest(0, 7919);
        tenThousandPrimesRequest = createRequest(0, 104729);
        tenPrimesRequest = createRequest(0, 29);
    }

    @Test
    public void testMillionPrimes() {
        assertEquals(task.execute(millionPrimesRequest).getResultMap().get("primeCount"), 1_000_000);
    }

    @Test
    public void testStop() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    task.stopProcess();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
        assertEquals(task.execute(millionPrimesRequest).getResultMap().get("primeCount"), 0);
        assertTrue(task.isStopped());
    }

    @Test
    public void testTenThousandPrimes() {
        assertEquals(task.execute(tenThousandPrimesRequest).getResultMap().get("primeCount"), 10000);
    }

    @Test
    public void testTenPrimes() {
        assertEquals(task.execute(tenPrimesRequest).getResultMap().get("primeCount"), 10);
    }

    @Test
    public void testThousandPrimes() {
        assertEquals(task.execute(thousandPrimesRequest).getResultMap().get("primeCount"), 1000);
    }

    @Test
    public void testValidateRequestNull() {
        ValidationResult validationResult = task.validateRequest(null);
        assertFalse(validationResult.isValid());
    }

    @Test
    public void testValidateRequestBadBegin() {
        Map<String, Serializable> addition = new HashMap<>();
        addition.put(BEGIN, 100000);
        addition.put(END, 0);
        NodeRequest nodeRequest = NodeRequest.regular(UUID.randomUUID(), "test", addition);
        ValidationResult validationResult = task.validateRequest(nodeRequest);
        assertFalse(validationResult.isValid());
    }

    @Test
    public void testValidateRequestOk() {
        Map<String, Serializable> addition = new HashMap<>();
        addition.put(BEGIN, 0);
        addition.put(END, 100000);
        NodeRequest nodeRequest = NodeRequest.regular(UUID.randomUUID(), "test", addition);
        ValidationResult validationResult = task.validateRequest(nodeRequest);
        assertTrue(validationResult.isValid());
    }


}
