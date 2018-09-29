package ru.babobka.nodeift;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.babobka.nodeconfigs.master.MasterServerConfig;
import ru.babobka.nodemasterserver.exception.TaskExecutionException;
import ru.babobka.nodemasterserver.server.MasterServer;
import ru.babobka.nodemasterserver.service.TaskService;
import ru.babobka.nodemasterserver.task.TaskExecutionResult;
import ru.babobka.nodesecurity.rsa.RSAPublicKey;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.data.Data;
import ru.babobka.nodetester.master.MasterServerRunner;
import ru.babobka.nodetester.slave.SlaveServerRunner;
import ru.babobka.nodetester.slave.cluster.SlaveServerCluster;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.enums.Env;
import ru.babobka.nodeutils.logger.NodeLogger;
import ru.babobka.nodeutils.logger.SimpleLoggerFactory;
import ru.babobka.nodeutils.util.TextUtil;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * Created by 123 on 08.11.2017.
 */
public class PrimeCounterITCase {

    protected static final int PRIME_COUNTER_LITTLE_RANGE_ANSWER = 25;
    protected static final int PRIME_COUNTER_MEDIUM_RANGE_ANSWER = 1229;
    protected static final int PRIME_COUNTER_LARGE_RANGE_ANSWER = 22044;
    protected static final int PRIME_COUNTER_EXTRA_LARGE_RANGE_ANSWER = 283146;
    private static final String TASK_NAME = "ru.babobka.primecounter.task.PrimeCounterTask";
    protected static MasterServer masterServer;
    private static TaskService taskService;

    @BeforeClass
    public static void setUp() throws IOException {
        Container.getInstance().put(SimpleLoggerFactory.debugLogger(PrimeCounterITCase.class.getSimpleName(), TextUtil.getEnv(Env.NODE_LOGS)));
        MasterServerRunner.init();
        MasterServerConfig masterServerConfig = Container.getInstance().get(MasterServerConfig.class);
        RSAPublicKey publicKey = masterServerConfig.getSecurity().getRsaConfig().getPublicKey();
        SlaveServerRunner.init(publicKey);
        masterServer = MasterServerRunner.runMasterServer();
        taskService = Container.getInstance().get(TaskService.class);
    }

    @AfterClass
    public static void tearDown() throws InterruptedException {
        masterServer.interrupt();
        masterServer.join();
        Container.getInstance().clear();
    }

    private static NodeRequest createPrimeCounterRequest(long begin, long end) {
        Data data = new Data();
        data.put("begin", begin);
        data.put("end", end);
        return NodeRequest.regular(UUID.randomUUID(), TASK_NAME, data);
    }

    static NodeRequest getLittleRangeRequest() {
        return createPrimeCounterRequest(0L, 100L);
    }

    static NodeRequest getMediumRangeRequest() {
        return createPrimeCounterRequest(0L, 10_000L);
    }

    static NodeRequest getLargeRangeRequest() {
        return createPrimeCounterRequest(0L, 250_000L);
    }

    static NodeRequest getExtraLargeRangeRequest() {
        return createPrimeCounterRequest(0L, 4_000_000L);
    }

    static NodeRequest getEnormousRangeRequest() {
        return createPrimeCounterRequest(0L, 15_000_000L);
    }

    @Test
    public void testCountPrimesLargeRangeOneSlave() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD)) {
            slaveServerCluster.start();
            NodeRequest request = getLargeRangeRequest();
            TaskExecutionResult result = taskService.executeTask(request);
            assertEquals((int) result.getData().get("primeCount"), PRIME_COUNTER_LARGE_RANGE_ANSWER);
        }
    }

    @Test
    public void testCountPrimesLittleRangeTwoSlaves() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD, 2)) {
            slaveServerCluster.start();
            NodeRequest request = getLittleRangeRequest();
            TaskExecutionResult result = taskService.executeTask(request);
            assertEquals((int) result.getData().get("primeCount"), PRIME_COUNTER_LITTLE_RANGE_ANSWER);
        }
    }

    @Test
    public void testCountPrimesLittleRangeOneSlaveMassive() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD)) {
            slaveServerCluster.start();
            NodeRequest request = getLittleRangeRequest();
            for (int i = 0; i < 50; i++) {
                TaskExecutionResult result = taskService.executeTask(request);
                assertEquals((int) result.getData().get("primeCount"), PRIME_COUNTER_LITTLE_RANGE_ANSWER);
            }
        }
    }

    @Test
    public void testCountPrimesLittleRangeTwoSlavesMassive() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD, 2)) {
            slaveServerCluster.start();
            NodeRequest request = getLittleRangeRequest();
            for (int i = 0; i < 50; i++) {
                TaskExecutionResult result = taskService.executeTask(request);
                assertEquals((int) result.getData().get("primeCount"), PRIME_COUNTER_LITTLE_RANGE_ANSWER);
            }
        }
    }

    @Test
    public void testCountPrimesRangeTwoSlavesMassive() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD, 2)) {
            slaveServerCluster.start();
            NodeRequest request = getLittleRangeRequest();
            for (int i = 0; i < 50; i++) {
                TaskExecutionResult result = taskService.executeTask(request);
                assertEquals((int) result.getData().get("primeCount"), PRIME_COUNTER_LITTLE_RANGE_ANSWER);
            }
        }
    }

    @Test
    public void testCountPrimesLittleRangeTwoSlavesMassiveGlitched() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD, 2, true)) {
            slaveServerCluster.start();
            NodeRequest request = getLittleRangeRequest();
            for (int i = 0; i < 50; i++) {
                TaskExecutionResult result = taskService.executeTask(request);
                assertEquals((int) result.getData().get("primeCount"), PRIME_COUNTER_LITTLE_RANGE_ANSWER);
            }
        }
    }

    @Test
    public void testCountPrimesLargeRangeTwoSlavesMassiveGlitched() throws IOException, TaskExecutionException, InterruptedException {
        NodeLogger nodeLogger = Container.getInstance().get(NodeLogger.class);
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD, 2, true)) {
            slaveServerCluster.start();
            for (int i = 0; i < 150; i++) {
                NodeRequest request = getLargeRangeRequest();
                nodeLogger.info("Tested request task id is [" + request.getTaskId() + "]");
                TaskExecutionResult result = taskService.executeTask(request);
                nodeLogger.info("Tested request task id is [" + request.getTaskId() + "] is done");
                assertEquals((int) result.getData().get("primeCount"), PRIME_COUNTER_LARGE_RANGE_ANSWER);
            }
        }
    }

    @Test
    public void testCountPrimesLargeRangeThreeSlavesMassiveGlitchedParallel() throws IOException, TaskExecutionException, InterruptedException {
        NodeLogger nodeLogger = Container.getInstance().get(NodeLogger.class);
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD, 3, true)) {
            final AtomicInteger failedTest = new AtomicInteger(0);
            slaveServerCluster.start();
            Thread[] threads = new Thread[10];
            for (int i = 0; i < threads.length; i++) {
                threads[i] = new Thread(() -> {
                    for (int j = 0; j < 250; j++) {
                        try {
                            NodeRequest request = getLargeRangeRequest();
                            nodeLogger.info("Tested request task id is [" + request.getTaskId() + "]");
                            TaskExecutionResult result = taskService.executeTask(request);
                            nodeLogger.info("Tested request task id is [" + request.getTaskId() + "] is done");
                            if (!result.getData().get("primeCount").equals(PRIME_COUNTER_LARGE_RANGE_ANSWER)) {
                                nodeLogger.warning("Tested request task id [" + request.getTaskId() + "] was failed");
                                failedTest.incrementAndGet();
                                break;
                            }
                        } catch (TaskExecutionException e) {
                            failedTest.incrementAndGet();
                            e.printStackTrace();
                            break;
                        }
                    }
                });
                threads[i].start();
            }
            for (Thread thread : threads) {
                thread.join();
            }
            assertEquals(failedTest.get(), 0);
        }
    }

    @Test
    public void testCountPrimesExtraLargeRangeTwoSlavesMassiveGlitchedParallel() throws IOException, TaskExecutionException, InterruptedException {
        NodeLogger nodeLogger = Container.getInstance().get(NodeLogger.class);
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD, 2, true)) {
            final AtomicInteger failedTest = new AtomicInteger(0);
            slaveServerCluster.start();
            Thread[] threads = new Thread[5];
            for (int i = 0; i < threads.length; i++) {
                threads[i] = new Thread(() -> {
                    for (int j = 0; j < 5; j++) {
                        try {
                            NodeRequest request = getExtraLargeRangeRequest();
                            nodeLogger.info("Tested request task id is [" + request.getTaskId() + "]");
                            TaskExecutionResult result = taskService.executeTask(request);
                            nodeLogger.info("Tested request task id is [" + request.getTaskId() + "] is done");
                            if (!result.getData().get("primeCount").equals(PRIME_COUNTER_EXTRA_LARGE_RANGE_ANSWER)) {
                                failedTest.incrementAndGet();
                                break;
                            }
                        } catch (TaskExecutionException e) {
                            failedTest.incrementAndGet();
                            e.printStackTrace();
                            break;
                        }
                    }
                });
                threads[i].start();
            }
            for (Thread thread : threads) {
                thread.join();
            }
            assertEquals(failedTest.get(), 0);
        }
    }

    @Test
    public void testCountPrimesExtraLargeRangeTwoSlavesMassiveGlitched() throws IOException, TaskExecutionException, InterruptedException {
        NodeLogger nodeLogger = Container.getInstance().get(NodeLogger.class);
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD, 2, true)) {
            slaveServerCluster.start();
            for (int i = 0; i < 25; i++) {
                NodeRequest request = getExtraLargeRangeRequest();
                nodeLogger.info("Tested request task id is [" + request.getTaskId() + "]");
                TaskExecutionResult result = taskService.executeTask(request);
                nodeLogger.info("Tested request task id is [" + request.getTaskId() + "] is done");
                assertEquals((int) result.getData().get("primeCount"), PRIME_COUNTER_EXTRA_LARGE_RANGE_ANSWER);
            }
        }
    }

    @Test
    public void testCountPrimesLargeRangeOneSlaveMassive() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD)) {
            slaveServerCluster.start();
            NodeRequest request = getLargeRangeRequest();
            for (int i = 0; i < 50; i++) {
                TaskExecutionResult result = taskService.executeTask(request);
                assertEquals((int) result.getData().get("primeCount"), PRIME_COUNTER_LARGE_RANGE_ANSWER);
            }
        }
    }

    @Test
    public void testCountPrimesLargeRangeTwoSlavesMassive() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD, 2)) {
            slaveServerCluster.start();
            NodeRequest request = getLargeRangeRequest();
            for (int i = 0; i < 50; i++) {
                TaskExecutionResult result = taskService.executeTask(request);
                assertEquals((int) result.getData().get("primeCount"), PRIME_COUNTER_LARGE_RANGE_ANSWER);
            }
        }
    }

    @Test(expected = TaskExecutionException.class)
    public void testCountPrimeInvalidRangeOneSlave() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD)) {
            slaveServerCluster.start();
            NodeRequest request = createPrimeCounterRequest(100L, 0L);
            taskService.executeTask(request);
        }
    }

    @Test(expected = TaskExecutionException.class)
    public void testCountPrimeInvalidRangeTwoSlaves() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD, 2)) {
            slaveServerCluster.start();
            NodeRequest request = createPrimeCounterRequest(100L, 0L);
            taskService.executeTask(request);
        }
    }

    @Test
    public void testCountPrimesMediumRangeOneSlave() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD)) {
            slaveServerCluster.start();
            NodeRequest request = getMediumRangeRequest();
            TaskExecutionResult result = taskService.executeTask(request);
            assertEquals((int) result.getData().get("primeCount"), PRIME_COUNTER_MEDIUM_RANGE_ANSWER);
        }
    }

    @Test
    public void testCountPrimeLargeRangeOneSlave() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD)) {
            slaveServerCluster.start();
            NodeRequest request = getLargeRangeRequest();
            TaskExecutionResult result = taskService.executeTask(request);
            assertEquals((int) result.getData().get("primeCount"), PRIME_COUNTER_LARGE_RANGE_ANSWER);
        }
    }

    @Test
    public void testCountPrimeLargeRangeTwoSlaves() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD, 2)) {
            slaveServerCluster.start();
            NodeRequest request = getLargeRangeRequest();
            TaskExecutionResult result = taskService.executeTask(request);
            assertEquals((int) result.getData().get("primeCount"), PRIME_COUNTER_LARGE_RANGE_ANSWER);
        }
    }

    @Test
    public void testCancelUnexistingTask() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD)) {
            slaveServerCluster.start();
            assertFalse(taskService.cancelTask(UUID.randomUUID()));
        }
    }

    @Test
    public void testCancelTask() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD)) {
            final AtomicBoolean taskFail = new AtomicBoolean(false);
            slaveServerCluster.start();
            NodeRequest request = getEnormousRangeRequest();
            Thread taskServiceThread = new Thread(() -> {
                try {
                    TaskExecutionResult result = taskService.executeTask(request);
                    if (!result.wasStopped()) {
                        taskFail.set(true);
                    }
                } catch (TaskExecutionException e) {
                    taskFail.set(true);
                    e.printStackTrace();
                }
            });
            taskServiceThread.start();
            Thread.sleep(1000L);
            assertTrue(taskService.cancelTask(request.getTaskId()));
            taskServiceThread.join();
            assertFalse(taskFail.get());
        }
    }

    @Test
    public void testCancelTaskTwoSlaves() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD, 2)) {
            final AtomicBoolean taskFail = new AtomicBoolean(false);
            slaveServerCluster.start();
            NodeRequest request = getEnormousRangeRequest();
            Thread taskServiceThread = new Thread(() -> {
                try {
                    TaskExecutionResult result = taskService.executeTask(request);
                    if (!result.wasStopped()) {
                        taskFail.set(true);
                    }
                } catch (TaskExecutionException e) {
                    taskFail.set(true);
                    e.printStackTrace();
                }
            });
            taskServiceThread.start();
            Thread.sleep(1000L);
            assertTrue(taskService.cancelTask(request.getTaskId()));
            taskServiceThread.join();
            assertFalse(taskFail.get());
        }
    }

    @Test
    public void testCancelTaskOneSlaveMassive() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD)) {
            slaveServerCluster.start();
            for (int i = 0; i < 15; i++) {
                final AtomicBoolean taskFail = new AtomicBoolean(false);
                NodeRequest request = getEnormousRangeRequest();
                Thread taskServiceThread = new Thread(() -> {
                    try {
                        TaskExecutionResult result = taskService.executeTask(request);
                        if (!result.wasStopped()) {
                            taskFail.set(true);
                        }
                    } catch (TaskExecutionException e) {
                        taskFail.set(true);
                        e.printStackTrace();
                    }
                });
                taskServiceThread.start();
                Thread.sleep(1000L);
                assertTrue(taskService.cancelTask(request.getTaskId()));
                taskServiceThread.join();
                assertFalse(taskFail.get());
            }
        }
    }

    @Test
    public void testCancelTaskTwoSlavesMassive() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD, 2)) {
            slaveServerCluster.start();
            for (int i = 0; i < 15; i++) {
                final AtomicBoolean taskFail = new AtomicBoolean(false);
                NodeRequest request = getEnormousRangeRequest();
                Thread taskServiceThread = new Thread(() -> {
                    try {
                        TaskExecutionResult result = taskService.executeTask(request);
                        if (!result.wasStopped()) {
                            taskFail.set(true);
                        }
                    } catch (TaskExecutionException e) {
                        taskFail.set(true);
                        e.printStackTrace();
                    }
                });
                taskServiceThread.start();
                Thread.sleep(1000L);
                assertTrue(taskService.cancelTask(request.getTaskId()));
                taskServiceThread.join();
                assertFalse(taskFail.get());
            }
        }
    }

    @Test
    public void testCountPrimeLargeRangeThreeSlavesMassiveParallel() throws IOException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD, 3)) {
            final AtomicInteger failedTests = new AtomicInteger();
            slaveServerCluster.start();
            Thread[] threads = new Thread[10];
            for (int i = 0; i < threads.length; i++) {
                threads[i] = new Thread(() -> {
                    try {
                        for (int i1 = 0; i1 < 5; i1++) {
                            NodeRequest request = getLargeRangeRequest();
                            TaskExecutionResult result = taskService.executeTask(request);
                            if (!result.getData().get("primeCount").equals(PRIME_COUNTER_LARGE_RANGE_ANSWER)) {
                                failedTests.incrementAndGet();
                            }
                        }
                    } catch (TaskExecutionException e) {
                        failedTests.incrementAndGet();
                        e.printStackTrace();
                    }
                });
                threads[i].start();
            }

            for (Thread thread : threads) {
                thread.join();
            }
            assertEquals(failedTests.get(), 0);
        }
    }

    @Test
    public void testCountPrimeLittleRangeTwoSlaves() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD, 2)) {
            slaveServerCluster.start();
            NodeRequest request = getLittleRangeRequest();
            TaskExecutionResult result = taskService.executeTask(request);
            assertEquals((int) result.getData().get("primeCount"), PRIME_COUNTER_LITTLE_RANGE_ANSWER);
        }
    }

    @Test
    public void testCountPrimeLargeRangeThreeSlaves() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD, 3)) {
            slaveServerCluster.start();
            NodeRequest request = getLargeRangeRequest();
            TaskExecutionResult result = taskService.executeTask(request);
            assertEquals((int) result.getData().get("primeCount"), PRIME_COUNTER_LARGE_RANGE_ANSWER);
        }
    }


}
