package ru.babobka.primecounter.model;

import org.junit.Test;
import ru.babobka.nodeserials.NodeRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by 123 on 20.06.2017.
 */
public class PrimeCounterDistributorTest {

    private static final String BEGIN = "begin";

    private static final String END = "end";

    private PrimeCounterDistributor primeCounterDistributor = new PrimeCounterDistributor("taskName");

    @Test
    public void testValidArgumentsEmpty() {
        assertFalse(primeCounterDistributor.validArguments(new HashMap<>()));
    }

    @Test
    public void testDistribute() {
        Map<String, String> addition = new HashMap<>();
        addition.put(BEGIN, "0");
        addition.put(END, "100000");
        int nodes = 5;
        NodeRequest[] nodeRequests = primeCounterDistributor.distribute(addition, nodes, UUID.randomUUID());
        assertEquals(nodeRequests.length, nodes);
    }

    @Test
    public void testValidArgumentsBadBegin() {
        Map<String, String> addition = new HashMap<>();
        addition.put(BEGIN, "100000");
        addition.put(END, "0");
        assertFalse(primeCounterDistributor.validArguments(addition));
    }

    @Test
    public void testValidArgumentsOk() {
        Map<String, String> addition = new HashMap<>();
        addition.put(BEGIN, "0");
        addition.put(END, "100000");
        assertTrue(primeCounterDistributor.validArguments(addition));
    }
}