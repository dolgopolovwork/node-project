package ru.babobka.dlp.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.babobka.dlp.model.PollardDlpDataValidators;
import ru.babobka.dlp.model.PollardDlpReducer;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodetask.exception.ReducingException;
import ru.babobka.nodeutils.container.Container;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by 123 on 28.01.2018.
 */
public class PollardDlpReducerTest {

    private PollardDlpDataValidators pollardDlpDataValidators;
    private PollardDlpReducer pollardDlpReducer;

    @Before
    public void setUp() {
        pollardDlpDataValidators = mock(PollardDlpDataValidators.class);
        Container.getInstance().put(pollardDlpDataValidators);
        pollardDlpReducer = new PollardDlpReducer();
    }

    @After
    public void tearDown() {
        Container.getInstance().clear();
    }

    @Test(expected = ReducingException.class)
    public void testReduceImplNoValidResponse() throws ReducingException {
        NodeResponse response = mock(NodeResponse.class);
        List<NodeResponse> responseList = Arrays.asList(response, response, response);
        when(pollardDlpDataValidators.isValidResponse(response)).thenReturn(false);
        pollardDlpReducer.reduceImpl(responseList);
    }

    @Test
    public void testReduceImpl() throws ReducingException {
        NodeResponse response = mock(NodeResponse.class);
        List<NodeResponse> responseList = Arrays.asList(response, response, response);
        when(pollardDlpDataValidators.isValidResponse(response)).thenReturn(true);
        assertNotNull(pollardDlpReducer.reduceImpl(responseList));
    }
}
