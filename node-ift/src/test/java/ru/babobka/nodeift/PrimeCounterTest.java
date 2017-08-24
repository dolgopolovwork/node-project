package ru.babobka.nodeift;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.babobka.nodeift.master.MasterServerRunner;
import ru.babobka.nodeift.slave.SlaveServerCluster;
import ru.babobka.nodeift.slave.SlaveServerRunner;
import ru.babobka.nodemasterserver.exception.TaskExecutionException;
import ru.babobka.nodemasterserver.server.MasterServer;
import ru.babobka.nodemasterserver.service.TaskService;
import ru.babobka.nodemasterserver.task.TaskExecutionResult;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeslaveserver.server.SlaveServer;
import ru.babobka.nodeutils.container.Container;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by 123 on 08.11.2017.
 */
public class PrimeCounterTest {

    private static MasterServer masterServer;
    private static TaskService taskService;
    private static final String LOGIN = "test_user";
    private static final String PASSWORD = "test_password";
    private static final String TASK_NAME = "ru.babobka.primecounter.task.PrimeCounterTask";
    private static final int PRIME_COUNTER_LITTLE_RANGE_ANSWER = 25;
    private static final int PRIME_COUNTER_MEDIUM_RANGE_ANSWER = 1229;
    private static final int PRIME_COUNTER_LARGE_RANGE_ANSWER = 22044;

    @BeforeClass
    public static void setUp() {
        MasterServerRunner.init();
        SlaveServerRunner.init();
        masterServer = MasterServerRunner.runMasterServer();
        taskService = Container.getInstance().get(TaskService.class);
    }

    @AfterClass
    public static void tearDown() {
        masterServer.interrupt();
    }

    @Test
    public void testCountPrimesLittleRangeOneSlave() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(LOGIN, PASSWORD)) {
            slaveServerCluster.start();
            NodeRequest request = getLargeRangeRequest();
            TaskExecutionResult result = taskService.executeTask(request);
            assertEquals(result.getResult().get("primeCount"), PRIME_COUNTER_LARGE_RANGE_ANSWER);
        }
    }

    @Test
    public void testCountPrimesLittleRangeTwoSlaves() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(LOGIN, PASSWORD, 2)) {
            slaveServerCluster.start();
            NodeRequest request = getLittleRangeRequest();
            TaskExecutionResult result = taskService.executeTask(request);
            assertEquals(result.getResult().get("primeCount"), PRIME_COUNTER_LITTLE_RANGE_ANSWER);
        }
    }

    @Test
    public void testCountPrimesLittleRangeOneSlaveMassive() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(LOGIN, PASSWORD)) {
            slaveServerCluster.start();
            NodeRequest request = getLittleRangeRequest();
            for (int i = 0; i < 100; i++) {
                TaskExecutionResult result = taskService.executeTask(request);
                assertEquals(result.getResult().get("primeCount"), PRIME_COUNTER_LITTLE_RANGE_ANSWER);
            }
        }
    }

    @Test
    public void testCountPrimesLittleRangeTwoSlavesMassive() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(LOGIN, PASSWORD, 2)) {
            slaveServerCluster.start();
            NodeRequest request = getLittleRangeRequest();
            for (int i = 0; i < 100; i++) {
                TaskExecutionResult result = taskService.executeTask(request);
                assertEquals(result.getResult().get("primeCount"), PRIME_COUNTER_LITTLE_RANGE_ANSWER);
            }
        }
    }

    @Test
    public void testCountPrimesLargeRangeOneSlaveMassive() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(LOGIN, PASSWORD)) {
            slaveServerCluster.start();
            NodeRequest request = getLargeRangeRequest();
            for (int i = 0; i < 100; i++) {
                TaskExecutionResult result = taskService.executeTask(request);
                assertEquals(result.getResult().get("primeCount"), PRIME_COUNTER_LARGE_RANGE_ANSWER);
            }
        }
    }

    @Test
    public void testCountPrimesLargeRangeTwoSlavesMassive() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(LOGIN, PASSWORD, 2)) {
            slaveServerCluster.start();
            NodeRequest request = getLargeRangeRequest();
            for (int i = 0; i < 100; i++) {
                TaskExecutionResult result = taskService.executeTask(request);
                assertEquals(result.getResult().get("primeCount"), PRIME_COUNTER_LARGE_RANGE_ANSWER);
            }
        }
    }

    @Test(expected = TaskExecutionException.class)
    public void testCountPrimeInvalidRangeOneSlave() throws IOException, TaskExecutionException, InterruptedException {
        SlaveServer slaveServer = SlaveServerRunner.runSlaveServer(LOGIN, PASSWORD);
        try {
            Thread.sleep(1000L);
            NodeRequest request = createPrimeCounterRequest(100L, 0L);
            taskService.executeTask(request);
        } finally {
            slaveServer.interrupt();
        }
    }

    @Test(expected = TaskExecutionException.class)
    public void testCountPrimeInvalidRangeTwoSlaves() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(LOGIN, PASSWORD, 2)) {
            slaveServerCluster.start();
            NodeRequest request = createPrimeCounterRequest(100L, 0L);
            taskService.executeTask(request);
        }
    }

    @Test
    public void testCountPrimesMediumRangeOneSlave() throws IOException, TaskExecutionException, InterruptedException {
        SlaveServer slaveServer = SlaveServerRunner.runSlaveServer(LOGIN, PASSWORD);
        try {
            Thread.sleep(1000L);
            NodeRequest request = getMediumRangeRequest();
            TaskExecutionResult result = taskService.executeTask(request);
            assertEquals(result.getResult().get("primeCount"), PRIME_COUNTER_MEDIUM_RANGE_ANSWER);
        } finally {
            slaveServer.interrupt();
        }
    }

    @Test
    public void testCountPrimeLargeRangeOneSlave() throws IOException, TaskExecutionException, InterruptedException {
        SlaveServer slaveServer = SlaveServerRunner.runSlaveServer(LOGIN, PASSWORD);
        try {
            Thread.sleep(1000L);
            NodeRequest request = getLargeRangeRequest();
            TaskExecutionResult result = taskService.executeTask(request);
            assertEquals(result.getResult().get("primeCount"), PRIME_COUNTER_LARGE_RANGE_ANSWER);
        } finally {
            slaveServer.interrupt();
        }
    }

    @Test
    public void testCountPrimeLargeRangeTwoSlaves() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(LOGIN, PASSWORD, 2)) {
            slaveServerCluster.start();
            NodeRequest request = getLargeRangeRequest();
            TaskExecutionResult result = taskService.executeTask(request);
            assertEquals(result.getResult().get("primeCount"), PRIME_COUNTER_LARGE_RANGE_ANSWER);
        }
    }

    @Test(expected = TaskExecutionException.class)
    public void testCancelUnexistingTask() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(LOGIN, PASSWORD)) {
            slaveServerCluster.start();
            taskService.cancelTask(UUID.randomUUID());
        }
    }

    @Test
    public void testCancelTask() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(LOGIN, PASSWORD)) {
            final AtomicBoolean taskFail = new AtomicBoolean(false);
            slaveServerCluster.start();
            NodeRequest request = getExtraLargeRangeRequest();
            Thread taskServiceThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        TaskExecutionResult result = taskService.executeTask(request);
                        if (!result.isWasStopped()) {
                            taskFail.set(true);
                        }
                    } catch (TaskExecutionException e) {
                        taskFail.set(true);
                        e.printStackTrace();
                    }
                }
            });
            taskServiceThread.start();
            Thread.sleep(1000L);
            taskService.cancelTask(request.getTaskId());
            taskServiceThread.join();
            assertFalse(taskFail.get());
        }
    }

    @Test
    public void testCancelTaskTwoSlaves() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(LOGIN, PASSWORD, 2)) {
            final AtomicBoolean taskFail = new AtomicBoolean(false);
            slaveServerCluster.start();
            NodeRequest request = getExtraLargeRangeRequest();
            Thread taskServiceThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        TaskExecutionResult result = taskService.executeTask(request);
                        if (!result.isWasStopped()) {
                            taskFail.set(true);
                        }
                    } catch (TaskExecutionException e) {
                        taskFail.set(true);
                        e.printStackTrace();
                    }
                }
            });
            taskServiceThread.start();
            Thread.sleep(1000L);
            taskService.cancelTask(request.getTaskId());
            taskServiceThread.join();
            assertFalse(taskFail.get());
        }
    }

    @Test
    public void testCountPrimeLargeRangeThreeSlavesMassiveParallel() throws IOException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(LOGIN, PASSWORD, 3)) {
            final AtomicInteger failedTests = new AtomicInteger();
            slaveServerCluster.start();
            Thread[] threads = new Thread[10];
            for (int i = 0; i < threads.length; i++) {
                threads[i] = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            for (int i = 0; i < 5; i++) {
                                NodeRequest request = getLargeRangeRequest();
                                TaskExecutionResult result = taskService.executeTask(request);
                                if (!result.getResult().get("primeCount").equals(PRIME_COUNTER_LARGE_RANGE_ANSWER)) {
                                    failedTests.incrementAndGet();
                                }
                            }
                        } catch (TaskExecutionException e) {
                            failedTests.incrementAndGet();
                            e.printStackTrace();
                        }
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
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(LOGIN, PASSWORD, 2)) {
            slaveServerCluster.start();
            NodeRequest request = getLittleRangeRequest();
            TaskExecutionResult result = taskService.executeTask(request);
            assertEquals(result.getResult().get("primeCount"), PRIME_COUNTER_LITTLE_RANGE_ANSWER);
        }
    }

    @Test
    public void testCountPrimeLargeRangeThreeSlaves() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(LOGIN, PASSWORD, 3)) {
            slaveServerCluster.start();
            NodeRequest request = getLargeRangeRequest();
            TaskExecutionResult result = taskService.executeTask(request);
            assertEquals(result.getResult().get("primeCount"), PRIME_COUNTER_LARGE_RANGE_ANSWER);
        }
    }

    private static NodeRequest createPrimeCounterRequest(long begin, long end) {
        Map<String, Serializable> data = new HashMap<>();
        data.put("begin", begin);
        data.put("end", end);
        return NodeRequest.regular(UUID.randomUUID(), TASK_NAME, data);
    }

    private static NodeRequest getLittleRangeRequest() {
        return createPrimeCounterRequest(0L, 100L);
    }

    private static NodeRequest getMediumRangeRequest() {
        return createPrimeCounterRequest(0L, 10_000L);
    }

    private static NodeRequest getLargeRangeRequest() {
        return createPrimeCounterRequest(0L, 250_000L);
    }

    private static NodeRequest getExtraLargeRangeRequest() {
        return createPrimeCounterRequest(0L, 15_000_000L);
    }

}
