package ru.babobka.factor.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.data.Data;
import ru.babobka.nodetask.exception.ReducingException;
import ru.babobka.nodeutils.container.Container;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by 123 on 05.01.2018.
 */
public class EllipticFactorReducerTest {
    private EllipticFactorDataValidators ellipticFactorDataValidators;
    private EllipticFactorReducer reducer;

    @Before
    public void setUp() {
        ellipticFactorDataValidators = mock(EllipticFactorDataValidators.class);
        Container.getInstance().put(ellipticFactorDataValidators);
        reducer = new EllipticFactorReducer();
    }

    @After
    public void tearDown() {
        Container.getInstance().clear();
    }

    @Test(expected = ReducingException.class)
    public void testReduceNoValidResponse() throws ReducingException {
        when(ellipticFactorDataValidators.isValidResponse(any(NodeResponse.class))).thenReturn(false);
        reducer.reduce(Arrays.asList(mock(NodeResponse.class), mock(NodeResponse.class)));
    }

    @Test
    public void testReduce() throws ReducingException {
        NodeResponse response = mock(NodeResponse.class);
        Data data = new Data();
        data.put("abc", 123);
        when(response.getData()).thenReturn(data);
        NodeResponse badResponse = mock(NodeResponse.class);
        when(ellipticFactorDataValidators.isValidResponse(response)).thenReturn(true);
        when(ellipticFactorDataValidators.isValidResponse(badResponse)).thenReturn(false);
        Data result = reducer.reduce(Arrays.asList(badResponse, badResponse, response));
        assertEquals((int) result.get("abc"), (int) data.get("abc"));
    }

}
