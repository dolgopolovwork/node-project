package ru.babobka.nodeift;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.babobka.nodebusiness.debug.DebugCredentials;
import ru.babobka.nodeconfigs.master.MasterServerConfig;
import ru.babobka.nodemasterserver.server.MasterServer;
import ru.babobka.nodesecurity.keypair.KeyDecoder;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.data.Data;
import ru.babobka.nodetask.service.TaskService;
import ru.babobka.nodetester.master.MasterServerRunner;
import ru.babobka.nodetester.slave.SlaveServerRunner;
import ru.babobka.nodetester.slave.cluster.SlaveServerCluster;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.enums.Env;
import ru.babobka.nodeutils.log.LoggerInit;
import ru.babobka.nodeutils.util.TextUtil;

import java.io.IOException;
import java.security.PublicKey;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * Created by 123 on 08.11.2017.
 */
public class PrimeCounterITCase {

    private static Logger logger = Logger.getLogger(PrimeCounterITCase.class);
    protected static final int PRIME_COUNTER_LITTLE_RANGE_ANSWER = 25;
    protected static final int PRIME_COUNTER_MEDIUM_RANGE_ANSWER = 1229;
    public static final int PRIME_COUNTER_LARGE_RANGE_ANSWER = 22044;
    protected static final int PRIME_COUNTER_EXTRA_LARGE_RANGE_ANSWER = 283146;
    private static final String TASK_NAME = "ru.babobka.primecounter.task.PrimeCounterTask";
    protected static MasterServer masterServer;
    private static TaskService taskService;

    @BeforeClass
    public static void setUp() throws IOException {
        LoggerInit.initPersistentConsoleDebugLogger(TextUtil.getEnv(Env.NODE_LOGS), PrimeCounterITCase.class.getSimpleName());
        MasterServerRunner.init();
        MasterServerConfig masterServerConfig = Container.getInstance().get(MasterServerConfig.class);
        PublicKey serverPublicKey = KeyDecoder.decodePublicKeyUnsafe(masterServerConfig.getKeyPair().getPubKey());
        SlaveServerRunner.init(serverPublicKey);
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

    public static NodeRequest getLargeRangeRequest() {
        return createPrimeCounterRequest(0L, 250_000L);
    }

    static NodeRequest getExtraLargeRangeRequest() {
        return createPrimeCounterRequest(0L, 4_000_000L);
    }

    public static NodeRequest getEnormousRangeRequest() {
        return createPrimeCounterRequest(0L, 15_000_000L);
    }

    @Test
    public void testCountPrimesLargeRangeOneSlave() throws IOException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(DebugCredentials.USER_NAME, DebugCredentials.PRIV_KEY)) {
            slaveServerCluster.start();
            NodeRequest request = getLargeRangeRequest();
            taskService.executeTask(request,
                    result -> assertEquals((int) result.getData().get("primeCount"), PRIME_COUNTER_LARGE_RANGE_ANSWER),
                    error -> {
                        logger.error(error);
                        fail();
                    });
        }
    }

    @Test
    public void testCountPrimesLittleRangeTwoSlaves() throws IOException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(DebugCredentials.USER_NAME, DebugCredentials.PRIV_KEY, 2)) {
            slaveServerCluster.start();
            NodeRequest request = getLittleRangeRequest();
            taskService.executeTask(request,
                    result -> assertEquals((int) result.getData().get("primeCount"), PRIME_COUNTER_LITTLE_RANGE_ANSWER),
                    error -> {
                        logger.error(error);
                        fail();
                    });
        }
    }

    @Test
    public void testCountPrimesLittleRangeOneSlaveMassive() throws IOException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(DebugCredentials.USER_NAME, DebugCredentials.PRIV_KEY)) {
            slaveServerCluster.start();
            NodeRequest request = getLittleRangeRequest();
            for (int i = 0; i < 50; i++) {
                taskService.executeTask(request,
                        result -> assertEquals((int) result.getData().get("primeCount"), PRIME_COUNTER_LITTLE_RANGE_ANSWER),
                        error -> {
                            logger.error(error);
                            fail();
                        });
            }
        }
    }

    @Test
    public void testCountPrimesLittleRangeTwoSlavesMassive() throws IOException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(DebugCredentials.USER_NAME, DebugCredentials.PRIV_KEY, 2)) {
            slaveServerCluster.start();
            NodeRequest request = getLittleRangeRequest();
            for (int i = 0; i < 50; i++) {
                taskService.executeTask(request,
                        result -> assertEquals((int) result.getData().get("primeCount"), PRIME_COUNTER_LITTLE_RANGE_ANSWER),
                        error -> {
                            logger.error(error);
                            fail();
                        });
            }
        }
    }

    @Test
    public void testCountPrimesRangeTwoSlavesMassive() throws IOException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(DebugCredentials.USER_NAME, DebugCredentials.PRIV_KEY, 2)) {
            slaveServerCluster.start();
            NodeRequest request = getLittleRangeRequest();
            for (int i = 0; i < 50; i++) {
                taskService.executeTask(request,
                        result -> assertEquals((int) result.getData().get("primeCount"), PRIME_COUNTER_LITTLE_RANGE_ANSWER),
                        error -> {
                            logger.error(error);
                            fail();
                        });
            }
        }
    }

    @Test
    public void testCountPrimesLittleRangeTwoSlavesMassiveGlitched() throws IOException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(DebugCredentials.USER_NAME, DebugCredentials.PRIV_KEY, 2, true)) {
            slaveServerCluster.start();
            NodeRequest request = getLittleRangeRequest();
            for (int i = 0; i < 50; i++) {
                taskService.executeTask(request,
                        result -> assertEquals((int) result.getData().get("primeCount"), PRIME_COUNTER_LITTLE_RANGE_ANSWER),
                        error -> {
                            logger.error(error);
                            fail();
                        });
            }
        }
    }

    @Test
    public void testCountPrimesLargeRangeTwoSlavesMassiveGlitched() throws IOException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(DebugCredentials.USER_NAME, DebugCredentials.PRIV_KEY, 2, true)) {
            slaveServerCluster.start();
            for (int i = 0; i < 150; i++) {
                NodeRequest request = getLargeRangeRequest();
                logger.info("Tested request task id is [" + request.getTaskId() + "]");
                taskService.executeTask(request, result -> {
                    logger.info("Tested request task id is [" + request.getTaskId() + "] is done");
                    assertEquals((int) result.getData().get("primeCount"), PRIME_COUNTER_LARGE_RANGE_ANSWER);
                }, error -> {
                    logger.error(error);
                    fail();
                });
            }
        }
    }

    @Test
    public void testCountPrimesLargeRangeThreeSlavesMassiveGlitchedParallel() throws IOException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(DebugCredentials.USER_NAME, DebugCredentials.PRIV_KEY, 3, true)) {
            final AtomicInteger failedTest = new AtomicInteger();
            slaveServerCluster.start();
            Thread[] threads = new Thread[10];
            for (int i = 0; i < threads.length; i++) {
                threads[i] = new Thread(() -> {
                    for (int j = 0; j < 250; j++) {
                        NodeRequest request = getLargeRangeRequest();
                        logger.info("Tested request task id is [" + request.getTaskId() + "]");
                        taskService.executeTask(request, result -> {
                                    logger.info("Tested request task id is [" + request.getTaskId() + "] is done");
                                    if (!result.getData().get("primeCount").equals(PRIME_COUNTER_LARGE_RANGE_ANSWER)) {
                                        logger.warn("Tested request task id [" + request.getTaskId() + "] was failed");
                                        failedTest.incrementAndGet();
                                    }
                                },
                                error -> {
                                    failedTest.incrementAndGet();
                                    logger.error(error);
                                });
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
    public void testCountPrimesExtraLargeRangeTwoSlavesMassiveGlitchedParallel() throws IOException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(DebugCredentials.USER_NAME, DebugCredentials.PRIV_KEY, 2, true)) {
            final AtomicInteger failedTest = new AtomicInteger();
            slaveServerCluster.start();
            Thread[] threads = new Thread[5];
            for (int i = 0; i < threads.length; i++) {
                threads[i] = new Thread(() -> {
                    for (int j = 0; j < 5; j++) {
                        NodeRequest request = getExtraLargeRangeRequest();
                        logger.info("Tested request task id is [" + request.getTaskId() + "]");
                        taskService.executeTask(request, result -> {
                            logger.info("Tested request task id is [" + request.getTaskId() + "] is done");
                            if (!result.getData().get("primeCount").equals(PRIME_COUNTER_EXTRA_LARGE_RANGE_ANSWER)) {
                                failedTest.incrementAndGet();
                            }
                        }, error -> {
                            logger.error(error);
                            failedTest.incrementAndGet();
                        });

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
    public void testCountPrimesExtraLargeRangeTwoSlavesMassiveGlitched() throws IOException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(DebugCredentials.USER_NAME, DebugCredentials.PRIV_KEY, 2, true)) {
            slaveServerCluster.start();
            for (int i = 0; i < 25; i++) {
                NodeRequest request = getExtraLargeRangeRequest();
                logger.info("Tested request task id is [" + request.getTaskId() + "]");
                taskService.executeTask(request, result -> {
                    logger.info("Tested request task id is [" + request.getTaskId() + "] is done");
                    assertEquals((int) result.getData().get("primeCount"), PRIME_COUNTER_EXTRA_LARGE_RANGE_ANSWER);
                }, error -> {
                    logger.error(error);
                    fail();
                });
            }
        }
    }

    @Test
    public void testCountPrimesLargeRangeOneSlaveMassive() throws IOException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(DebugCredentials.USER_NAME, DebugCredentials.PRIV_KEY)) {
            slaveServerCluster.start();
            NodeRequest request = getLargeRangeRequest();
            for (int i = 0; i < 50; i++) {
                taskService.executeTask(request,
                        result -> assertEquals((int) result.getData().get("primeCount"), PRIME_COUNTER_LARGE_RANGE_ANSWER),
                        error -> {
                            logger.error(error);
                            fail();
                        });
            }
        }
    }

    @Test
    public void testCountPrimesLargeRangeTwoSlavesMassive() throws IOException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(DebugCredentials.USER_NAME, DebugCredentials.PRIV_KEY, 2)) {
            slaveServerCluster.start();
            NodeRequest request = getLargeRangeRequest();
            for (int i = 0; i < 50; i++) {
                taskService.executeTask(request,
                        result -> assertEquals((int) result.getData().get("primeCount"), PRIME_COUNTER_LARGE_RANGE_ANSWER),
                        error -> {
                            logger.error(error);
                            fail();
                        });
            }
        }
    }

    @Test
    public void testCountPrimeInvalidRangeOneSlave() throws IOException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(DebugCredentials.USER_NAME, DebugCredentials.PRIV_KEY)) {
            slaveServerCluster.start();
            NodeRequest request = createPrimeCounterRequest(100L, 0L);
            taskService.executeTask(request,
                    result -> {
                        fail();
                    },
                    error -> {
                    });
        }
    }

    @Test
    public void testCountPrimeInvalidRangeTwoSlaves() throws IOException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(DebugCredentials.USER_NAME, DebugCredentials.PRIV_KEY, 2)) {
            slaveServerCluster.start();
            NodeRequest request = createPrimeCounterRequest(100L, 0L);
            taskService.executeTask(request, response -> fail(), error -> {
            });
        }
    }

    @Test
    public void testCountPrimesMediumRangeOneSlave() throws IOException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(DebugCredentials.USER_NAME, DebugCredentials.PRIV_KEY)) {
            slaveServerCluster.start();
            NodeRequest request = getMediumRangeRequest();
            taskService.executeTask(request,
                    result -> assertEquals((int) result.getData().get("primeCount"), PRIME_COUNTER_MEDIUM_RANGE_ANSWER),
                    error -> {
                        logger.error(error);
                        fail();
                    });
        }
    }

    @Test
    public void testCountPrimeLargeRangeOneSlave() throws IOException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(DebugCredentials.USER_NAME, DebugCredentials.PRIV_KEY)) {
            slaveServerCluster.start();
            NodeRequest request = getLargeRangeRequest();
            taskService.executeTask(request,
                    result -> assertEquals((int) result.getData().get("primeCount"), PRIME_COUNTER_LARGE_RANGE_ANSWER),
                    error -> {
                        logger.error(error);
                        fail();
                    });
        }
    }

    @Test
    public void testCountPrimeLargeRangeTwoSlaves() throws IOException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(DebugCredentials.USER_NAME, DebugCredentials.PRIV_KEY, 2)) {
            slaveServerCluster.start();
            NodeRequest request = getLargeRangeRequest();
            taskService.executeTask(request,
                    result -> assertEquals((int) result.getData().get("primeCount"), PRIME_COUNTER_LARGE_RANGE_ANSWER),
                    error -> {
                        logger.error(error);
                        fail();
                    });
        }
    }

    @Test
    public void testCancelNotExistingTask() throws IOException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(DebugCredentials.USER_NAME, DebugCredentials.PRIV_KEY)) {
            slaveServerCluster.start();
            taskService.cancelTask(UUID.randomUUID(), Assert::assertFalse, error -> {
                logger.error(error);
                fail();
            });
        }
    }

    @Test
    public void testCancelTask() throws IOException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(DebugCredentials.USER_NAME, DebugCredentials.PRIV_KEY)) {
            final AtomicBoolean taskFail = new AtomicBoolean(false);
            slaveServerCluster.start();
            NodeRequest request = getEnormousRangeRequest();
            Thread taskServiceThread = new Thread(() -> taskService.executeTask(request, result -> {
                if (!result.wasStopped()) {
                    taskFail.set(true);
                }
            }, error -> {
                logger.error(error);
                taskFail.set(true);
            }));
            taskServiceThread.start();
            Thread.sleep(1000L);
            taskService.cancelTask(request.getTaskId(), Assert::assertTrue, error -> {
                logger.error(error);
                fail();
            });
            taskServiceThread.join();
            assertFalse(taskFail.get());
        }
    }

    @Test
    public void testCancelTaskTwoSlaves() throws IOException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(DebugCredentials.USER_NAME, DebugCredentials.PRIV_KEY, 2)) {
            final AtomicBoolean taskFail = new AtomicBoolean(false);
            slaveServerCluster.start();
            NodeRequest request = getEnormousRangeRequest();
            Thread taskServiceThread = new Thread(() -> taskService.executeTask(request, result -> {
                if (!result.wasStopped()) {
                    taskFail.set(true);
                }
            }, error -> {
                logger.error(error);
                taskFail.set(true);
            }));

            taskServiceThread.start();
            Thread.sleep(1000L);
            taskService.cancelTask(request.getTaskId(), Assert::assertTrue, error -> {
                logger.error(error);
                fail();
            });
            taskServiceThread.join();
            assertFalse(taskFail.get());
        }
    }

    @Test
    public void testCancelTaskOneSlaveMassive() throws IOException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(DebugCredentials.USER_NAME, DebugCredentials.PRIV_KEY)) {
            slaveServerCluster.start();
            for (int i = 0; i < 15; i++) {
                final AtomicBoolean taskFail = new AtomicBoolean(false);
                NodeRequest request = getEnormousRangeRequest();
                Thread taskServiceThread = new Thread(() -> {
                    taskService.executeTask(request, result -> {
                        if (!result.wasStopped()) {
                            taskFail.set(true);
                        }
                    }, error -> {
                        logger.error(error);
                        taskFail.set(true);
                    });

                });
                taskServiceThread.start();
                Thread.sleep(1000L);
                taskService.cancelTask(request.getTaskId(), Assert::assertTrue, error -> {
                    logger.error(error);
                    fail();
                });
                taskServiceThread.join();
                assertFalse(taskFail.get());
            }
        }
    }

    @Test
    public void testCancelTaskTwoSlavesMassive() throws IOException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(DebugCredentials.USER_NAME, DebugCredentials.PRIV_KEY, 2)) {
            slaveServerCluster.start();
            for (int i = 0; i < 15; i++) {
                final AtomicBoolean taskFail = new AtomicBoolean(false);
                NodeRequest request = getEnormousRangeRequest();
                Thread taskServiceThread = new Thread(() -> {
                    taskService.executeTask(request, result -> {
                        if (!result.wasStopped()) {
                            taskFail.set(true);
                        }
                    }, error -> {
                        logger.error(error);
                        taskFail.set(true);
                    });
                });
                taskServiceThread.start();
                Thread.sleep(1000L);
                taskService.cancelTask(request.getTaskId(), Assert::assertTrue, error -> {
                    logger.error(error);
                    fail();
                });
                taskServiceThread.join();
                assertFalse(taskFail.get());
            }
        }
    }

    @Test
    public void testCountPrimeLargeRangeThreeSlavesMassiveParallel() throws IOException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(DebugCredentials.USER_NAME, DebugCredentials.PRIV_KEY, 3)) {
            final AtomicInteger failedTests = new AtomicInteger();
            slaveServerCluster.start();
            Thread[] threads = new Thread[10];
            for (int i = 0; i < threads.length; i++) {
                threads[i] = new Thread(() -> {
                    for (int i1 = 0; i1 < 5; i1++) {
                        NodeRequest request = getLargeRangeRequest();
                        taskService.executeTask(request, result -> {
                            if (!result.getData().get("primeCount").equals(PRIME_COUNTER_LARGE_RANGE_ANSWER)) {
                                failedTests.incrementAndGet();
                            }
                        }, error -> {
                            logger.error(error);
                            failedTests.incrementAndGet();
                        });
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
    public void testCountPrimeLittleRangeTwoSlaves() throws IOException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(DebugCredentials.USER_NAME, DebugCredentials.PRIV_KEY, 2)) {
            slaveServerCluster.start();
            NodeRequest request = getLittleRangeRequest();
            taskService.executeTask(request,
                    result -> assertEquals((int) result.getData().get("primeCount"), PRIME_COUNTER_LITTLE_RANGE_ANSWER),
                    error -> {
                        logger.error(error);
                        fail();
                    });
        }
    }

    @Test
    public void testCountPrimeLargeRangeThreeSlaves() throws IOException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(DebugCredentials.USER_NAME, DebugCredentials.PRIV_KEY, 3)) {
            slaveServerCluster.start();
            NodeRequest request = getLargeRangeRequest();
            taskService.executeTask(request,
                    result -> assertEquals((int) result.getData().get("primeCount"), PRIME_COUNTER_LARGE_RANGE_ANSWER),
                    error -> {
                        logger.error(error);
                        fail();
                    });
        }
    }
}
