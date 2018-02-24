package ru.babobka.node.dlp.benchmark;

import ru.babobka.nodeclient.Client;
import ru.babobka.nodemasterserver.server.MasterServerConfig;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.enumerations.ResponseStatus;
import ru.babobka.nodetester.benchmark.NodeBenchmark;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.time.Timer;
import ru.babobka.nodeutils.util.MathUtil;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Future;

import static ru.babobka.nodeclient.CLI.print;
import static ru.babobka.nodeclient.CLI.printErr;

/**
 * Created by 123 on 01.02.2018.
 */
public class DlpNodeBenchmark extends NodeBenchmark {
    private static final Random RAND = new Random();
    private static final String TASK_NAME = "ru.babobka.dlp.task.PollardDlpTask";
    private final int tests;
    private final int orderBitLength;

    public DlpNodeBenchmark(int tests, int orderBitLength) {
        this.tests = tests;
        this.orderBitLength = orderBitLength;
    }

    @Override
    protected void onBenchmark() {
        MasterServerConfig masterServerConfig = Container.getInstance().get(MasterServerConfig.class);
        MathUtil.SafePrime safePrime = MathUtil.getSafePrime(orderBitLength);
        BigInteger gen = MathUtil.getGenerator(safePrime);
        int port = masterServerConfig.getClientListenerPort();
        long totalTime = 0;
        try (Client client = createClient("localhost", port)) {
            for (int test = 0; test < tests; test++) {
                Future<NodeResponse> future = client.executeTask(createDlpRequest(gen, createNumber(safePrime.getPrime()), safePrime.getPrime()));
                Timer timer = new Timer();
                NodeResponse response = future.get();
                totalTime += timer.getTimePassed();
                if (response.getStatus() != ResponseStatus.NORMAL) {
                    printErr("Can not get the result. Response is " + response);
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        print(safePrime.getPrime().bitLength() + " bit order takes " +
                (totalTime / (double) tests) + "mls");
    }

    private BigInteger createNumber(BigInteger mod) {
        BigInteger number = BigInteger.valueOf(RAND.nextInt()).mod(mod);
        if (number.equals(BigInteger.ZERO)) {
            return createNumber(mod);
        }
        return number;
    }

    private static NodeRequest createDlpRequest(BigInteger x, BigInteger y, BigInteger mod) {
        Map<String, Serializable> data = new HashMap<>();
        data.put("x", x);
        data.put("y", y);
        data.put("mod", mod);
        return NodeRequest.regular(UUID.randomUUID(), TASK_NAME, data);
    }
}
