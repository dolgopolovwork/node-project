package ru.babobka.nodeift.container.master;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import ru.babobka.nodeclient.rpc.RpcClient;
import ru.babobka.nodeift.container.AbstractContainerITCase;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.enumerations.ResponseStatus;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;
import static ru.babobka.nodeift.EllipticCurveITCase.createFactorTest;
import static ru.babobka.nodeift.PrimeCounterITCase.*;

public class MasterOneSlaveRMQITCase extends AbstractContainerITCase {

    private static final Logger logger = Logger.getLogger(MasterOneSlaveRMQITCase.class);

    private static final GenericContainer postgres = createPostgres();
    private static final GenericContainer master = createMasterWithRMQ();
    private static final GenericContainer slave = createSlave();
    private static final GenericContainer rmq = createRMQ();

    @BeforeClass
    public static void runContainers() throws InterruptedException {
        postgres.start();
        rmq.start();
        master.start();
        Thread.sleep(MASTER_SERVER_WAIT_MILLIS);
        slave.start();
        Thread.sleep(SLAVE_SERVER_WAIT_MILLIS);
    }

    @AfterClass
    public static void stopContainer() {
        postgres.stop();
        master.close();
        rmq.close();
        slave.close();
    }

    @Test
    public void testFactor() throws IOException, InterruptedException, TimeoutException {
        assertTrue(isMasterHealthy(master));
        assertEquals(1, getMasterClusterSize(master));
        int executedTaskSize = getMasterTaskMonitoring(master).getExecutedTasks();
        try (RpcClient client = new RpcClient("localhost", getRmqPort(rmq), RPC_REPLY_QUEUE)) {
            int bits = 45;
            BigInteger p = BigInteger.probablePrime(bits, new Random());
            BigInteger q = BigInteger.probablePrime(bits, new Random());
            NodeRequest request = createFactorTest(p, q);
            NodeResponse response = client.call(request);
            BigInteger factor = response.getDataValue("factor");
            assertTrue(factor.equals(p) || factor.equals(q));
        }
        assertEquals(executedTaskSize + 1, getMasterTaskMonitoring(master).getExecutedTasks());
    }

    @Test
    public void testFactorMassive() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        assertTrue(isMasterHealthy(master));
        assertEquals(1, getMasterClusterSize(master));
        int executedTaskSize = getMasterTaskMonitoring(master).getExecutedTasks();
        int tests = 25;
        try (RpcClient client = new RpcClient("localhost", getRmqPort(rmq), RPC_REPLY_QUEUE)) {
            int bits = 35;
            for (int i = 0; i < tests; i++) {
                BigInteger p = BigInteger.probablePrime(bits, new Random());
                BigInteger q = BigInteger.probablePrime(bits, new Random());
                NodeRequest request = createFactorTest(p, q);
                NodeResponse response = client.call(request);
                BigInteger factor = response.getDataValue("factor");
                assertTrue(factor.equals(p) || factor.equals(q));
            }
        }
        assertEquals(executedTaskSize + tests, getMasterTaskMonitoring(master).getExecutedTasks());
    }

    @Test
    public void testFactorMassiveParallel() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        assertTrue(isMasterHealthy(master));
        assertEquals(1, getMasterClusterSize(master));
        int executedTaskSize = getMasterTaskMonitoring(master).getExecutedTasks();
        int tests = 25;
        int threads = 4;
        CountDownLatch doneExecuting = new CountDownLatch(threads);
        AtomicBoolean failedTest = new AtomicBoolean(false);
        try (RpcClient client = new RpcClient("localhost", getRmqPort(rmq), RPC_REPLY_QUEUE)) {
            int bits = 35;
            for (int t = 0; t < threads; t++) {
                new Thread(() -> {
                    try {
                        for (int i = 0; i < tests; i++) {
                            BigInteger p = BigInteger.probablePrime(bits, new Random());
                            BigInteger q = BigInteger.probablePrime(bits, new Random());
                            NodeRequest request = createFactorTest(p, q);
                            NodeResponse response = client.call(request);
                            BigInteger factor = response.getDataValue("factor");
                            assertTrue(factor.equals(p) || factor.equals(q));
                        }
                    } catch (Exception e) {
                        logger.error("cannot execute test", e);
                        failedTest.set(true);
                    } finally {
                        doneExecuting.countDown();
                    }
                }).start();
            }
            doneExecuting.await();
            assertFalse(failedTest.get());
        }
        assertEquals(executedTaskSize + (tests * threads), getMasterTaskMonitoring(master).getExecutedTasks());
    }

    @Test
    public void testPrimeCount() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        assertTrue(isMasterHealthy(master));
        assertEquals(1, getMasterClusterSize(master));
        int executedTaskSize = getMasterTaskMonitoring(master).getExecutedTasks();
        try (RpcClient client = new RpcClient("localhost", getRmqPort(rmq), RPC_REPLY_QUEUE)) {
            NodeResponse response = client.call(getLargeRangeRequest());
            assertEquals((int) response.getDataValue("primeCount"), PRIME_COUNTER_LARGE_RANGE_ANSWER);
        }
        assertEquals(executedTaskSize + 1, getMasterTaskMonitoring(master).getExecutedTasks());
    }

    @Test
    public void testPrimeCountCancel() throws IOException, InterruptedException, TimeoutException {
        assertTrue(isMasterHealthy(master));
        assertEquals(1, getMasterClusterSize(master));
        int executedTaskSize = getMasterTaskMonitoring(master).getExecutedTasks();
        AtomicBoolean failedCancel = new AtomicBoolean();
        CountDownLatch cancelAwait = new CountDownLatch(1);
        try (RpcClient client = new RpcClient("localhost", getRmqPort(rmq), RPC_REPLY_QUEUE)) {
            NodeRequest request = getEnormousRangeRequest();
            new Thread(() -> {
                try {
                    Thread.sleep(1_000);
                    client.cancelCall(request.getTaskId());
                } catch (Exception ex) {
                    logger.error("cannot execute test", ex);
                    failedCancel.set(true);
                } finally {
                    cancelAwait.countDown();
                }
            }).start();
            NodeResponse response = client.call(request);
            assertEquals(ResponseStatus.STOPPED, response.getStatus());
            cancelAwait.await();
            assertFalse(failedCancel.get());
        }
        assertEquals(executedTaskSize + 1, getMasterTaskMonitoring(master).getExecutedTasks());
    }

    @Test
    public void testFactorTooBigRequest() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        assertTrue(isMasterHealthy(master));
        assertEquals(1, getMasterClusterSize(master));
        int executedTaskSize = getMasterTaskMonitoring(master).getExecutedTasks();
        try (RpcClient client = new RpcClient("localhost", getRmqPort(rmq), RPC_REPLY_QUEUE)) {
            int bits = 256;
            BigInteger p = BigInteger.probablePrime(bits, new Random());
            BigInteger q = BigInteger.probablePrime(bits, new Random());
            NodeRequest request = createFactorTest(p, q);
            NodeResponse response = client.call(request);
            assertEquals(response.getStatus(), ResponseStatus.VALIDATION_ERROR);
        }
        assertEquals(executedTaskSize, getMasterTaskMonitoring(master).getExecutedTasks());
    }
}
