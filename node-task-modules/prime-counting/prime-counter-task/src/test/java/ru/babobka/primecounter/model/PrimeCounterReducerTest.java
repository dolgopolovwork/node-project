package ru.babobka.primecounter.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodetask.exception.ReducingException;
import ru.babobka.nodetask.model.ReducingResult;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.primecounter.task.Params;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by 123 on 23.10.2017.
 */
public class PrimeCounterReducerTest {

    private PrimeCounterReducer primeCounterReducer;
    private PrimeCounterDataValidators primeCounterDataValidators;

    @Before
    public void setUp() {
        primeCounterDataValidators = mock(PrimeCounterDataValidators.class);
        Container.getInstance().put(primeCounterDataValidators);
        primeCounterReducer = new PrimeCounterReducer();
    }


    @After
    public void tearDown() {
        Container.getInstance().clear();
    }

    @Test(expected = ReducingException.class)
    public void testReduceNotValidResponse() throws ReducingException {
        when(primeCounterDataValidators.isValidResponse(any(NodeResponse.class))).thenReturn(false);
        primeCounterReducer.reduce(Arrays.asList(mock(NodeResponse.class), mock(NodeResponse.class)));
    }

    @Test
    public void testReduce() throws ReducingException {
        NodeResponse response = mock(NodeResponse.class);
        when(response.getDataValue(Params.PRIME_COUNT.getValue())).thenReturn(10);
        when(primeCounterDataValidators.isValidResponse(any(NodeResponse.class))).thenReturn(true);
        ReducingResult reducingResult = primeCounterReducer.reduce(Arrays.asList(response, response));
        assertEquals(reducingResult.get(Params.PRIME_COUNT.getValue()), 20);
    }
}