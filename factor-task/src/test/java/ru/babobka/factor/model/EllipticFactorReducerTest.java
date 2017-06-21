package ru.babobka.factor.model;

import org.junit.Test;
import ru.babobka.factor.task.EllipticCurveFactorTask;
import ru.babobka.factor.task.Params;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.subtask.exception.ReducingException;
import ru.babobka.subtask.model.ReducingResult;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by 123 on 20.06.2017.
 */
public class EllipticFactorReducerTest {

    private EllipticFactorReducer ellipticFactorReducer = new EllipticFactorReducer();

    @Test
    public void testValidResponseNull() {
        assertFalse(ellipticFactorReducer.validResponse(null));
    }

    @Test
    public void testValidResponseBadStatus() {
        assertFalse(ellipticFactorReducer.validResponse(NodeResponse.failed(UUID.randomUUID())));
    }

    @Test
    public void testValidResponseNoData() {
        assertFalse(ellipticFactorReducer.validResponse(NodeResponse.dummy(UUID.randomUUID())));
    }

    @Test
    public void testValidResponseBadFactor() {
        Map<String, Serializable> dataMap = new HashMap<>();
        dataMap.put(Params.FACTOR.getValue(), BigInteger.valueOf(3));
        dataMap.put(Params.NUMBER.getValue(), BigInteger.valueOf(1024));
        NodeResponse nodeResponse = NodeResponse.normal(dataMap, NodeRequest.heartBeatRequest(), 10L);
        assertFalse(ellipticFactorReducer.validResponse(nodeResponse));
    }

    @Test
    public void testValidResponseOk() {
        Map<String, Serializable> dataMap = new HashMap<>();
        dataMap.put(Params.FACTOR.getValue(), BigInteger.valueOf(2));
        dataMap.put(Params.NUMBER.getValue(), BigInteger.valueOf(1024));
        NodeResponse nodeResponse = NodeResponse.normal(dataMap, NodeRequest.heartBeatRequest(), 10L);
        assertTrue(ellipticFactorReducer.validResponse(nodeResponse));
    }

    @Test
    public void testReduceNull() {
        try {
            ellipticFactorReducer.reduce(null);
            fail();
        } catch (ReducingException e) {

        }
    }


    @Test
    public void testReduceFailOk() throws ReducingException {
        Map<String, Serializable> dataMap = new HashMap<>();
        dataMap.put(Params.FACTOR.getValue(), BigInteger.valueOf(2));
        dataMap.put(Params.NUMBER.getValue(), BigInteger.valueOf(1024));
        NodeResponse nodeResponse = NodeResponse.normal(dataMap, NodeRequest.heartBeatRequest(), 10L);
        List<NodeResponse> responses = new ArrayList<>(Arrays.asList(NodeResponse.failed(UUID.randomUUID()), nodeResponse));
        ReducingResult reducingResult = ellipticFactorReducer.reduce(responses);
        assertEquals(dataMap, reducingResult.map());
    }

    @Test
    public void testReduceAllFails() {
        NodeResponse nodeResponse = NodeResponse.failed(UUID.randomUUID());
        List<NodeResponse> responses = new ArrayList<>(Arrays.asList(nodeResponse, nodeResponse));
        try {
            ellipticFactorReducer.reduce(responses);
            fail();
        } catch (ReducingException e) {

        }

    }

    @Test
    public void testReduceOk() throws ReducingException {
        Map<String, Serializable> dataMap = new HashMap<>();
        dataMap.put(Params.FACTOR.getValue(), BigInteger.valueOf(2));
        dataMap.put(Params.NUMBER.getValue(), BigInteger.valueOf(1024));
        NodeResponse nodeResponse = NodeResponse.normal(dataMap, NodeRequest.heartBeatRequest(), 10L);
        List<NodeResponse> responses = new ArrayList<>(Arrays.asList(nodeResponse, nodeResponse, nodeResponse));
        ReducingResult reducingResult = ellipticFactorReducer.reduce(responses);
        assertEquals(dataMap, reducingResult.map());
    }
}
