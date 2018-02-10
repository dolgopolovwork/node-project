package ru.babobka.node.primecounter.benchmark;

import ru.babobka.nodeclient.Client;
import ru.babobka.nodemasterserver.server.MasterServerConfig;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.enumerations.ResponseStatus;
import ru.babobka.nodetester.benchmark.NodeBenchmark;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.time.Timer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Future;

import static ru.babobka.nodeclient.CLI.print;
import static ru.babobka.nodeclient.CLI.printErr;

/**
 * Created by 123 on 01.02.2018.
 */
public class PrimeCounterNodeBenchmark extends NodeBenchmark {

    private static final String TASK_NAME = "ru.babobka.primecounter.task.PrimeCounterTask";
    private final int begin;
    private final int end;
    private final int tests;

    public PrimeCounterNodeBenchmark(int tests, int begin, int end) {
        this.tests = tests;
        this.begin = begin;
        this.end = end;
    }

    @Override
    protected void onBenchmark() {
        MasterServerConfig masterServerConfig = Container.getInstance().get(MasterServerConfig.class);
        Timer timer = new Timer();
        int port = masterServerConfig.getClientListenerPort();
        try (Client client = createClient("localhost", port)) {
            for (int test = 0; test < tests; test++) {
                Future<NodeResponse> future = client.executeTask(createPrimeCounterRequest(begin, end));
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
        print("range [" + begin + ";" + end + "] takes " +
                (timer.getTimePassed() / (double) tests) + "mls");
    }

    private static Client createClient(String host, int port) {
        return new Client(host, port);
    }

    private static NodeRequest createPrimeCounterRequest(long begin, long end) {
        Map<String, Serializable> data = new HashMap<>();
        data.put("begin", begin);
        data.put("end", end);
        return NodeRequest.regular(UUID.randomUUID(), TASK_NAME, data);
    }
}
