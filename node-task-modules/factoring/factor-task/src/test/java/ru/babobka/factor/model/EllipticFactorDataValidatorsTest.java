package ru.babobka.factor.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.babobka.factor.task.Params;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.data.Data;
import ru.babobka.nodeutils.container.Container;

import java.math.BigInteger;
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
        assertFalse(ellipticFactorDataValidators.isValidResponse(NodeResponse.validationError(UUID.randomUUID())));
    }

    @Test
    public void testValidResponseNoData() {
        assertFalse(ellipticFactorDataValidators.isValidResponse(NodeResponse.dummy(UUID.randomUUID())));
    }

    @Test
    public void testValidResponseBadFactor() {
        Data data = new Data();
        data.put(Params.FACTOR.getValue(), BigInteger.valueOf(3));
        data.put(Params.NUMBER.getValue(), BigInteger.valueOf(1024));
        NodeResponse nodeResponse = NodeResponse.normal(data, NodeRequest.heartBeat(), 10L);
        assertFalse(ellipticFactorDataValidators.isValidResponse(nodeResponse));
    }

    @Test
    public void testValidResponseOk() {
        Data data = new Data();
        data.put(Params.FACTOR.getValue(), BigInteger.valueOf(2));
        data.put(Params.NUMBER.getValue(), BigInteger.valueOf(1024));
        NodeResponse nodeResponse = NodeResponse.normal(data, NodeRequest.heartBeat(), 10L);
        assertTrue(ellipticFactorDataValidators.isValidResponse(nodeResponse));
    }
}
