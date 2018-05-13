package ru.babobka.node.dlp.benchmark;

import ru.babobka.nodeclient.Client;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.enumerations.ResponseStatus;
import ru.babobka.nodetester.benchmark.NodeBenchmark;
import ru.babobka.nodeutils.math.SafePrime;
import ru.babobka.nodeutils.time.Timer;
import ru.babobka.nodeutils.util.MathUtil;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by 123 on 01.02.2018.
 */
public class DlpNodeBenchmark extends NodeBenchmark {
    private static final Random RAND = new Random();
    private static final String TASK_NAME = "ru.babobka.dlp.task.PollardDlpTask";
    private final int orderBitLength;
    private final SafePrime safePrime;
    private final BigInteger gen;

    public DlpNodeBenchmark(String appName, int tests, int orderBitLength) {
        super(appName, tests);
        this.orderBitLength = orderBitLength;
        safePrime = SafePrime.random(orderBitLength - 1);
        gen = MathUtil.getGenerator(safePrime);
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

    @Override
    protected String getDescription() {
        return "dlp in " + orderBitLength + " bit order group";
    }

    @Override
    protected void onBenchmark(Client client, AtomicLong timerStorage) throws IOException, ExecutionException, InterruptedException {
        Future<NodeResponse> future = client.executeTask(createDlpRequest(gen, createNumber(safePrime.getPrime()), safePrime.getPrime()));
        Timer timer = new Timer();
        NodeResponse response = future.get();
        timerStorage.addAndGet(timer.getTimePassed());
        if (response.getStatus() != ResponseStatus.NORMAL) {
            String message = "cannot get the result. Response is " + response;
            throw new IOException(message);
        }
    }
}
