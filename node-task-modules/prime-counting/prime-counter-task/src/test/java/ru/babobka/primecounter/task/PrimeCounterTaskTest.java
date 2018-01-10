package ru.babobka.primecounter.task;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeutils.container.ApplicationContainer;
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
        new ApplicationContainer() {
            @Override
            public void contain(Container container) {
                container.put(mock(PrimeCounterReducer.class));
                container.put(mock(PrimeCounterDistributor.class));
                container.put(mock(PrimeCounterDataValidators.class));
                container.put(mock(PrimeCounterTaskExecutor.class));
                container.put(mock(PrimeCounterServiceFactory.class));
            }
        }.contain(Container.getInstance());
        primeCounterTask = new PrimeCounterTask();
    }

    @After
    public void tearDown() {
        Container.getInstance().clear();
    }

    @Test
    public void testIsRequestDataTooSmallBigRange() {
        NodeRequest request = mock(NodeRequest.class);
        when(request.getStringDataValue(Params.BEGIN.getValue())).thenReturn("0");
        when(request.getStringDataValue(Params.END.getValue())).thenReturn("1000000");
        assertFalse(primeCounterTask.isRequestDataTooSmall(request));
    }

    @Test
    public void testIsRequestDataTooSmall() {
        NodeRequest request = mock(NodeRequest.class);
        when(request.getStringDataValue(Params.BEGIN.getValue())).thenReturn("0");
        when(request.getStringDataValue(Params.END.getValue())).thenReturn("1000");
        assertTrue(primeCounterTask.isRequestDataTooSmall(request));
    }
}
