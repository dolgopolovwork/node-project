package ru.babobka.primecounter.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodetask.model.ExecutionResult;
import ru.babobka.nodeutils.container.ApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.primecounter.service.PrimeCounterServiceFactory;
import ru.babobka.primecounter.service.PrimeCounterTaskService;
import ru.babobka.primecounter.task.Params;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created by 123 on 25.10.2017.
 */
public class PrimeCounterTaskExecutorTest {

    private PrimeCounterTaskService primeCounterTaskService;
    private PrimeCounterTaskExecutor primeCounterTaskExecutor;
    private PrimeCounterServiceFactory primeCounterServiceFactory;

    @Before
    public void setUp() {
        primeCounterTaskService = mock(PrimeCounterTaskService.class);
        primeCounterServiceFactory = mock(PrimeCounterServiceFactory.class);
        when(primeCounterServiceFactory.get()).thenReturn(primeCounterTaskService);
        new ApplicationContainer() {
            @Override
            public void contain(Container container) {
                container.put(primeCounterServiceFactory);
            }
        }.contain(Container.getInstance());
        primeCounterTaskExecutor = new PrimeCounterTaskExecutor();
    }

    @After
    public void tearDown() {
        Container.getInstance().clear();
    }

    @Test
    public void testExecute() {
        NodeRequest request = mock(NodeRequest.class);
        int result = 10;
        when(request.getStringDataValue(Params.BEGIN.getValue())).thenReturn("0");
        when(request.getStringDataValue(Params.END.getValue())).thenReturn("100");
        when(primeCounterTaskService.execute(any(Range.class))).thenReturn(result);
        ExecutionResult executionResult = primeCounterTaskExecutor.execute(request);
        assertEquals(executionResult.getResultMap().get(Params.PRIME_COUNT.getValue()), result);
        assertEquals(executionResult.isStopped(), primeCounterTaskService.isStopped());
    }

    @Test
    public void testStop() {
        primeCounterTaskExecutor.stopCurrentTask();
        verify(primeCounterTaskService).stop();
    }
}