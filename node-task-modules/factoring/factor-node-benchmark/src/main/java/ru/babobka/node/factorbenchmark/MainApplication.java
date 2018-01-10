package ru.babobka.node.factorbenchmark;

import ru.babobka.nodeclient.Client;
import ru.babobka.nodeclient.ClientApplicationContainer;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.enumerations.ResponseStatus;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.time.Timer;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Future;

public class MainApplication {

    private static final String TASK_NAME = "ru.babobka.factor.task.EllipticCurveFactorTask";

    static {
        Container.getInstance().put(new ClientApplicationContainer());
    }

    public static void main(String[] args) {
        FactorBenchmarkData benchmarkData;
        try {
            benchmarkData = new FactorBenchmarkData(args);
        } catch (IllegalArgumentException e) {
            System.err.print(e.getMessage());
            return;
        }
        BigInteger number = createBigInteger(benchmarkData.getNumberBitLength());
        Timer timer = new Timer();
        try (Client client = createClient(benchmarkData.getHost(), benchmarkData.getPort())) {
            for (int i = 0; i < benchmarkData.getIterations(); i++) {
                Future<NodeResponse> future = client.executeTask(createFactorRequest(number));
                NodeResponse response = future.get();
                if (response.getStatus() != ResponseStatus.NORMAL) {
                    System.err.println("Can not get the result. Response is " + response);
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        System.out.println(benchmarkData.getNumberBitLength() + " bit number takes " + (timer.getTimePassed() / (double) benchmarkData.getIterations()) + "mls");
    }

    private static BigInteger createBigInteger(int bits) {
        int factorLength = bits / 2;
        BigInteger p = BigInteger.probablePrime(factorLength, new Random());
        BigInteger q = BigInteger.probablePrime(factorLength, new Random());
        return p.multiply(q);
    }

    private static Client createClient(String host, int port) {
        return new Client(host, port);
    }

    private static NodeRequest createFactorRequest(BigInteger number) {
        Map<String, Serializable> data = new HashMap<>();
        data.put("number", number);
        return NodeRequest.regular(UUID.randomUUID(), TASK_NAME, data);
    }
}
