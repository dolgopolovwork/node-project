package ru.babobka.node.factorbenchmark;

import ru.babobka.dlp.Client;
import ru.babobka.nodemasterserver.server.MasterServerConfig;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.enumerations.ResponseStatus;
import ru.babobka.nodetester.benchmark.NodeBenchmark;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.time.Timer;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Future;

import static ru.babobka.dlp.CLI.print;
import static ru.babobka.dlp.CLI.printErr;

/**
 * Created by 123 on 01.02.2018.
 */
public class FactorNodeBenchmark extends NodeBenchmark {

    private static final String TASK_NAME = "ru.babobka.factor.task.EllipticCurveFactorTask";
    private final int tests;
    private final int numberBitLength;

    public FactorNodeBenchmark(int tests, int numberBitLength) {
        this.tests = tests;
        this.numberBitLength = numberBitLength;
    }

    @Override
    protected void onBenchmark() {
        MasterServerConfig masterServerConfig = Container.getInstance().get(MasterServerConfig.class);
        BigInteger number = createBigInteger(numberBitLength);
        Timer timer = new Timer();
        int port = masterServerConfig.getClientListenerPort();
        try (Client client = createClient("localhost", port)) {
            for (int test = 0; test < tests; test++) {
                Future<NodeResponse> future = client.executeTask(createFactorRequest(number));
                NodeResponse response = future.get();
                if (response.getStatus() != ResponseStatus.NORMAL) {
                    printErr("Can not get the result. Response is " + response);
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        print(numberBitLength + " bit number takes " +
                (timer.getTimePassed() / (double) tests) + "mls");
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
