package ru.babobka.factor.task;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.babobka.factor.model.EllipticFactorDataValidators;
import ru.babobka.factor.model.EllipticFactorDistributor;
import ru.babobka.factor.model.EllipticFactorReducer;
import ru.babobka.factor.service.EllipticCurveFactorServiceFactory;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.data.Data;
import ru.babobka.nodetask.model.SubTask;
import ru.babobka.nodeutils.container.Container;

import java.math.BigInteger;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class EllipticCurveFactorTaskTest {

    private SubTask task;

    @Before
    public void setUp() {
        Container.getInstance().put(mock(EllipticCurveFactorServiceFactory.class));
        Container.getInstance().put(mock(EllipticFactorDistributor.class));
        Container.getInstance().put(mock(EllipticFactorReducer.class));
        Container.getInstance().put(mock(EllipticFactorDataValidators.class));
        task = new EllipticCurveFactorTask();
    }

    @After
    public void tearDown() {
        Container.getInstance().clear();
    }

    @Test
    public void testIsRequestDataTooSmallLittleNumber() {
        Data data = new Data();
        data.put(Params.NUMBER.getValue(), new BigInteger("123"));
        NodeRequest request = NodeRequest.regular(UUID.randomUUID(), "test", data);
        assertTrue(task.isSingleNodeTask(request));
    }

    @Test
    public void testIsRequestDataTooSmallBigNumber() {
        Data data = new Data();
        data.put(Params.NUMBER.getValue(), BigInteger.probablePrime(100, new Random()));
        NodeRequest request = NodeRequest.regular(UUID.randomUUID(), "test", data);
        assertFalse(task.isSingleNodeTask(request));
    }

}