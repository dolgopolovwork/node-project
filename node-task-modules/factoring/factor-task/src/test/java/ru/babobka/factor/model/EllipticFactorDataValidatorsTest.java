package ru.babobka.factor.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.babobka.factor.task.Params;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by 123 on 25.10.2017.
 */
public class EllipticFactorDataValidatorsTest {
    private EllipticFactorDataValidators ellipticFactorDataValidators;

    @Before
    public void setUp() {
        Container.getInstance().put(mock(SimpleLogger.class));
        ellipticFactorDataValidators = new EllipticFactorDataValidators();
    }

    @After
    public void tearDown() {
        Container.getInstance().clear();
    }

    @Test
    public void testValidArgumentsEmpty() {
        assertFalse(ellipticFactorDataValidators.isValidRequest(mock(NodeRequest.class)));
    }

    @Test
    public void testValidArgumentsNegative() {
        NodeRequest request = mock(NodeRequest.class);
        when(request.getDataValue(Params.NUMBER.getValue())).thenReturn(BigInteger.valueOf(-1L));
        assertFalse(ellipticFactorDataValidators.isValidRequest(request));
    }

    @Test
    public void testValidArgumentsOk() {
        NodeRequest request = mock(NodeRequest.class);
        when(request.getDataValue(Params.NUMBER.getValue())).thenReturn(BigInteger.valueOf(123L));
        assertTrue(ellipticFactorDataValidators.isValidRequest(request));
    }

    @Test
    public void testValidResponseBadStatus() {
        assertFalse(ellipticFactorDataValidators.isValidResponse(NodeResponse.failed(UUID.randomUUID())));
    }

    @Test
    public void testValidResponseNoData() {
        assertFalse(ellipticFactorDataValidators.isValidResponse(NodeResponse.dummy(UUID.randomUUID())));
    }

    @Test
    public void testValidResponseBadFactor() {
        Map<String, Serializable> dataMap = new HashMap<>();
        dataMap.put(Params.FACTOR.getValue(), BigInteger.valueOf(3));
        dataMap.put(Params.NUMBER.getValue(), BigInteger.valueOf(1024));
        NodeResponse nodeResponse = NodeResponse.normal(dataMap, NodeRequest.heartBeat(), 10L);
        assertFalse(ellipticFactorDataValidators.isValidResponse(nodeResponse));
    }

    @Test
    public void testValidResponseOk() {
        Map<String, Serializable> dataMap = new HashMap<>();
        dataMap.put(Params.FACTOR.getValue(), BigInteger.valueOf(2));
        dataMap.put(Params.NUMBER.getValue(), BigInteger.valueOf(1024));
        NodeResponse nodeResponse = NodeResponse.normal(dataMap, NodeRequest.heartBeat(), 10L);
        assertTrue(ellipticFactorDataValidators.isValidResponse(nodeResponse));
    }
}
