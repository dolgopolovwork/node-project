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
import java.util.UUID;
import java.util.concurrent.Future;

import static ru.babobka.nodeclient.CLI.print;
import static ru.babobka.nodeclient.CLI.printErr;

/**
 * Created by 123 on 01.02.2018.
 */
public class DlpNodeBenchmark extends NodeBenchmark {

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
        Timer timer = new Timer();
        int port = masterServerConfig.getClientListenerPort();
        try (Client client = createClient("localhost", port)) {
            for (int test = 0; test < tests; test++) {
                Future<NodeResponse> future = client.executeTask(createDlpRequest(gen, BigInteger.TEN, safePrime.getPrime()));
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
        print(orderBitLength + " bit order takes " +
                (timer.getTimePassed() / (double) tests) + "mls");
    }


    private static ru.babobka.nodeclient.Client createClient(String host, int port) {
        return new Client(host, port);
    }

    private static NodeRequest createDlpRequest(BigInteger x, BigInteger y, BigInteger mod) {
        Map<String, Serializable> data = new HashMap<>();
        data.put("x", x);
        data.put("y", y);
        data.put("mod", mod);
        return NodeRequest.regular(UUID.randomUUID(), TASK_NAME, data);
    }
}
