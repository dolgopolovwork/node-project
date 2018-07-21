package ru.babobka.nodeift;

import org.junit.Test;
import ru.babobka.dlp.model.dist.DlpTaskDist;
import ru.babobka.nodemasterserver.exception.TaskExecutionException;
import ru.babobka.nodemasterserver.service.TaskService;
import ru.babobka.nodemasterserver.task.TaskExecutionResult;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.data.Data;
import ru.babobka.nodetester.slave.cluster.SlaveServerCluster;
import ru.babobka.nodeutils.math.Fp;
import ru.babobka.nodeutils.math.SafePrime;
import ru.babobka.nodeutils.util.MathUtil;

import java.io.IOException;
import java.math.BigInteger;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by 123 on 11.07.2018.
 */
public class PollardDlpDistITCase extends PollardDlpITCase {

    @Override
    protected NodeRequest createDlpRequest(int modBitLength) {
        NodeRequest request = createDlpRequest(modBitLength, Integer.MAX_VALUE);
        return NodeRequest.regular(UUID.randomUUID(), "ru.babobka.dlp.task.dist.PollardDistDlpTask", request.getData());
    }

    private NodeRequest createDlpRequest(int modBitLength, int loops) {
        NodeRequest request = super.createDlpRequest(modBitLength);
        Data data = request.getData();
        data.put("loops", loops);
        return NodeRequest.regular(UUID.randomUUID(), "ru.babobka.dlp.task.dist.PollardDistDlpTask", data);
    }

    public static DlpTaskDist createDlpTaskDist(int modBitLength, int loops) {
        SafePrime safePrime = SafePrime.random(modBitLength);
        BigInteger gen = MathUtil.getGenerator(safePrime);
        BigInteger mod = safePrime.getPrime();
        return new DlpTaskDist(
                new Fp(gen, mod),
                new Fp(BigInteger.valueOf(32), mod),
                loops);
    }

    public static DlpTaskDist createDlpTaskDist(int modBitLength) {
        SafePrime safePrime = SafePrime.random(modBitLength);
        BigInteger gen = MathUtil.getGenerator(safePrime);
        BigInteger mod = safePrime.getPrime();
        return new DlpTaskDist(
                new Fp(gen, mod),
                new Fp(BigInteger.valueOf(32), mod));
    }

    private void createDlpTest(int modBitLength, int loops, TaskService taskService) throws TaskExecutionException {
        NodeRequest request = createDlpRequest(modBitLength, loops);
        TaskExecutionResult result = taskService.executeTask(request);
        BigInteger exp = result.getData().get("exp");
        if (exp != null) {
            BigInteger x = result.getData().get("x");
            BigInteger y = result.getData().get("y");
            BigInteger mod = result.getData().get("mod");
            assertEquals(x.modPow(exp, mod), y);
        } else {
            assertNotNull(result.getData().get("collisions"));
        }
    }

    @Test
    public void testDlpDistMediumNumberOneSlaveUnlikelyToHaveResponse() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD)) {
            slaveServerCluster.start();
            int bits = 25;
            int loops = 1;
            createDlpTest(bits, loops, taskService);
        }
    }

    @Test
    public void testDlpDistMediumNumberOneSlaveUnlikelyToHaveResponseMassive() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD)) {
            slaveServerCluster.start();
            int bits = 25;
            int loops = 1;
            for (int i = 0; i < 25; i++) {
                createDlpTest(bits, loops, taskService);
            }
        }
    }

    @Test
    public void testDlpDistMediumNumberOneSlaveLikelyToHaveResponseMassive() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD)) {
            slaveServerCluster.start();
            int bits = 25;
            int loops = 10;
            for (int i = 0; i < 25; i++) {
                createDlpTest(bits, loops, taskService);
            }
        }
    }

    @Test
    public void testDlpDistBigNumberOneSlaveUnlikelyToHaveResponseMassive() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD)) {
            slaveServerCluster.start();
            int bits = 35;
            int loops = 1;
            for (int i = 0; i < 25; i++) {
                createDlpTest(bits, loops, taskService);
            }
        }
    }

    @Test
    public void testDlpDistBigNumberOneSlaveLikelyToHaveResponseMassive() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD)) {
            slaveServerCluster.start();
            int bits = 35;
            int loops = 100;
            for (int i = 0; i < 25; i++) {
                createDlpTest(bits, loops, taskService);
            }
        }
    }
}