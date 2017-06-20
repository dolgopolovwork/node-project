package ru.babobka.factor.model;

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
public class EllipticFactorDistributorTest {

    private static final String NUMBER = "number";

    private EllipticFactorDistributor ellipticFactorDistributor = new EllipticFactorDistributor("test");

    @Test
    public void testValidArgumentsNull() {
        assertFalse(ellipticFactorDistributor.validArguments(null));
    }

    @Test
    public void testValidArgumentsEmpty() {
        assertFalse(ellipticFactorDistributor.validArguments(new HashMap<>()));
    }

    @Test
    public void testValidArgumentsNegative() {
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put(NUMBER, "-1");
        assertFalse(ellipticFactorDistributor.validArguments(dataMap));
    }

    @Test
    public void testValidArgumentsOk() {
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put(NUMBER, "123");
        assertTrue(ellipticFactorDistributor.validArguments(dataMap));
    }

    @Test
    public void testDistribute() {
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put(NUMBER, "123");
        UUID id = UUID.randomUUID();
        int nodes = 5;
        NodeRequest[] nodeRequests = ellipticFactorDistributor.distribute(dataMap, nodes, id);
        assertEquals(nodeRequests.length, nodes);
        for (NodeRequest request : nodeRequests) {
            assertEquals(request.getTaskId(), id);
        }
    }
}