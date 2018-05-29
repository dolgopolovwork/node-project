package ru.babobka.node.factor.benchmark;

import ru.babobka.nodeclient.Client;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.data.Data;
import ru.babobka.nodeserials.enumerations.ResponseStatus;
import ru.babobka.nodetester.benchmark.NodeBenchmark;
import ru.babobka.nodeutils.time.Timer;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

import static ru.babobka.nodeclient.CLI.printErr;

/**
 * Created by 123 on 01.02.2018.
 */
public class FactorNodeBenchmark extends NodeBenchmark {

    private static final String TASK_NAME = "ru.babobka.factor.task.EllipticCurveFactorTask";
    private final int numberBitLength;

    public FactorNodeBenchmark(String appName, int tests, int numberBitLength) {
        super(appName, tests);
        this.numberBitLength = numberBitLength;
    }

    private static BigInteger createBigInteger(int bits) {
        int factorLength = bits / 2;
        BigInteger p = BigInteger.probablePrime(factorLength, new Random());
        BigInteger q = BigInteger.probablePrime(factorLength, new Random());
        return p.multiply(q);
    }

    private static NodeRequest createFactorRequest(BigInteger number) {
        Data data = new Data();
        data.put("number", number);
        return NodeRequest.regular(UUID.randomUUID(), TASK_NAME, data);
    }

    @Override
    protected String getDescription() {
        return numberBitLength + " bit number factoring";
    }

    @Override
    protected void onBenchmark(Client client, AtomicLong timerStorage) throws IOException, ExecutionException, InterruptedException {
        Future<NodeResponse> future = client.executeTask(createFactorRequest(createBigInteger(numberBitLength)));
        Timer requestTimer = new Timer();
        NodeResponse response = future.get();
        timerStorage.addAndGet(requestTimer.getTimePassed());
        if (response.getStatus() != ResponseStatus.NORMAL) {
            String message = "cannot getData the result. response is " + response;
            printErr(message);
            throw new IOException(message);
        }
    }
}
