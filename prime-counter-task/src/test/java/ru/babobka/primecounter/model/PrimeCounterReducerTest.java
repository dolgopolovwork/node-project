package ru.babobka.primecounter.model;

import org.junit.Test;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.primecounter.task.PrimeCounterTask;
import ru.babobka.subtask.exception.ReducingException;
import ru.babobka.subtask.model.ReducingResult;

import java.io.Serializable;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by 123 on 20.06.2017.
 */
public class PrimeCounterReducerTest {

    private final PrimeCounterReducer primeCounterReducer = new PrimeCounterReducer();

    @Test
    public void testIsValidNull() {
        assertFalse(primeCounterReducer.validResponse(null));
    }

    @Test
    public void testIsValidBadStatus() {
        NodeResponse response = NodeResponse.failed(UUID.randomUUID());
        assertFalse(primeCounterReducer.validResponse(response));
    }

    @Test
    public void testIsValidBadPrimeCount() {
        NodeResponse response = NodeResponse.dummy(UUID.randomUUID());
        assertFalse(primeCounterReducer.validResponse(response));
    }

    @Test
    public void testIsValidOk() {
        Map<String, Serializable> result = new HashMap<>();
        result.put(PrimeCounterTask.PRIME_COUNT, 10);
        NodeResponse response = NodeResponse.normal(result, NodeRequest.heartBeatRequest(), 10);
        assertTrue(primeCounterReducer.validResponse(response));
    }

    @Test
    public void testReduceNotValid() {
        NodeResponse response = NodeResponse.failed(UUID.randomUUID());
        List<NodeResponse> responses = new LinkedList<>(Arrays.asList(response, response, response));
        try {
            primeCounterReducer.reduce(responses);
            fail();
        } catch (ReducingException e) {

        }
    }




    @Test
    public void testReduceValid() throws ReducingException {
        Map<String, Serializable> result = new HashMap<>();
        result.put(PrimeCounterTask.PRIME_COUNT, 10);
        NodeResponse response = NodeResponse.normal(result, NodeRequest.heartBeatRequest(), 10);
        List<NodeResponse> responses = new LinkedList<>(Arrays.asList(response, response, response));
        ReducingResult reducingResult = primeCounterReducer.reduce(responses);
        assertEquals(reducingResult.get(PrimeCounterTask.PRIME_COUNT), 30);
    }


}
