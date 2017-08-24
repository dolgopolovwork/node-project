package ru.babobka.factor.task;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.babobka.factor.model.FactoringResult;
import ru.babobka.factor.model.ec.EllipticCurvePoint;
import ru.babobka.factor.model.ec.multprovider.MultiplicationProvider;
import ru.babobka.factor.service.EllipticCurveFactorService;
import ru.babobka.factor.service.EllipticCurveFactorServiceFactory;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodetask.model.ExecutionResult;
import ru.babobka.nodeutils.container.ApplicationContainer;
import ru.babobka.nodeutils.container.Container;

import java.math.BigInteger;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by 123 on 14.10.2017.
 */
public class EllipticCurveFactorTaskExecutorTest {

    private EllipticCurveFactorService service;
    private EllipticCurveFactorTaskExecutor executor;
    private EllipticCurveFactorServiceFactory factory;

    @Before
    public void setUp() {
        service = mock(EllipticCurveFactorService.class);
        factory = mock(EllipticCurveFactorServiceFactory.class);
        when(factory.get()).thenReturn(service);
        new ApplicationContainer() {
            @Override
            public void contain(Container container) {
                container.put(factory);
                container.put(mock(MultiplicationProvider.class));
            }
        }.contain(Container.getInstance());
        executor = new EllipticCurveFactorTaskExecutor();
    }

    @After
    public void tearDown() {
        Container.getInstance().clear();
    }

    @Test
    public void testStopCurrentTask() {
        executor.stopCurrentTask();
        verify(service).stop();
    }

    @Test
    public void testExecuteNullResult() {
        NodeRequest request = mock(NodeRequest.class);
        when(request.getStringDataValue(Params.NUMBER.getValue())).thenReturn("123");
        when(service.execute(any(BigInteger.class))).thenReturn(null);
        ExecutionResult result = executor.execute(request);
        assertTrue(result.isStopped());
        verify(service).stop();
    }

    @Test
    public void testExecute() {
        NodeRequest request = mock(NodeRequest.class);
        when(request.getStringDataValue(Params.NUMBER.getValue())).thenReturn("123");
        BigInteger factor = BigInteger.TEN;
        FactoringResult factoringResult = new FactoringResult(factor, EllipticCurvePoint.generateRandomPoint(BigInteger.TEN));
        when(service.execute(any(BigInteger.class))).thenReturn(factoringResult);
        ExecutionResult result = executor.execute(request);
        assertFalse(result.isStopped());
        assertEquals(result.getResultMap().get(Params.FACTOR.getValue()), factor);
        verify(service).stop();
    }
}
