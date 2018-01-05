package ru.babobka.factor.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodetask.exception.ReducingException;
import ru.babobka.nodetask.model.ReducingResult;
import ru.babobka.nodeutils.container.ApplicationContainer;
import ru.babobka.nodeutils.container.Container;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
        new ApplicationContainer() {
            @Override
            public void contain(Container container) {
                container.put(ellipticFactorDataValidators);
            }
        }.contain(Container.getInstance());
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
        Map<String, Serializable> dataMap = new HashMap<>();
        dataMap.put("abc", 123);
        when(response.getData()).thenReturn(dataMap);
        NodeResponse badResponse = mock(NodeResponse.class);
        when(ellipticFactorDataValidators.isValidResponse(response)).thenReturn(true);
        when(ellipticFactorDataValidators.isValidResponse(badResponse)).thenReturn(false);
        ReducingResult result = reducer.reduce(Arrays.asList(badResponse, badResponse, response));
        assertEquals(result.get("abc"), dataMap.get("abc"));
    }

}
