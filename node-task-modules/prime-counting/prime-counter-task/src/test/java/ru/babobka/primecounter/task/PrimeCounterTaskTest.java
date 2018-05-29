package ru.babobka.primecounter.task;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.primecounter.model.PrimeCounterDataValidators;
import ru.babobka.primecounter.model.PrimeCounterDistributor;
import ru.babobka.primecounter.model.PrimeCounterReducer;
import ru.babobka.primecounter.model.PrimeCounterTaskExecutor;
import ru.babobka.primecounter.service.PrimeCounterServiceFactory;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by 123 on 25.10.2017.
 */
public class PrimeCounterTaskTest {
    private PrimeCounterTask primeCounterTask;

    @Before
    public void setUp() {
        Container.getInstance().put(mock(PrimeCounterReducer.class));
        Container.getInstance().put(mock(PrimeCounterDistributor.class));
        Container.getInstance().put(mock(PrimeCounterDataValidators.class));
        Container.getInstance().put(mock(PrimeCounterTaskExecutor.class));
        Container.getInstance().put(mock(PrimeCounterServiceFactory.class));
        primeCounterTask = new PrimeCounterTask();
    }

    @After
    public void tearDown() {
        Container.getInstance().clear();
    }

    @Test
    public void testIsRequestDataTooSmallBigRange() {
        NodeRequest request = mock(NodeRequest.class);
        when(request.getDataValue(Params.BEGIN.getValue())).thenReturn(0L);
        when(request.getDataValue(Params.END.getValue())).thenReturn(1000000L);
        assertFalse(primeCounterTask.isRequestDataTooSmall(request));
    }

    @Test
    public void testIsRequestDataTooSmall() {
        NodeRequest request = mock(NodeRequest.class);
        when(request.getDataValue(Params.BEGIN.getValue())).thenReturn(0L);
        when(request.getDataValue(Params.END.getValue())).thenReturn(1000L);
        assertTrue(primeCounterTask.isRequestDataTooSmall(request));
    }
}
