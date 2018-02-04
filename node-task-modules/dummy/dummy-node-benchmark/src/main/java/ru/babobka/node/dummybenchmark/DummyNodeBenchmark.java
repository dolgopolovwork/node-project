package ru.babobka.node.dummybenchmark;

import ru.babobka.nodeclient.Client;
import ru.babobka.nodemasterserver.server.MasterServerConfig;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.enumerations.ResponseStatus;
import ru.babobka.nodetester.benchmark.NodeBenchmark;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.time.Timer;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.Future;

import static ru.babobka.nodeclient.CLI.print;
import static ru.babobka.nodeclient.CLI.printErr;

/**
 * Created by 123 on 01.02.2018.
 */
public class DummyNodeBenchmark extends NodeBenchmark {

    private static final String TASK_NAME = "ru.babobka.dummy.DummyTask";
    private final int tests;

    public DummyNodeBenchmark(int tests) {
        this.tests = tests;
    }

    @Override
    protected void onBenchmark() {
        MasterServerConfig masterServerConfig = Container.getInstance().get(MasterServerConfig.class);
        Timer timer = new Timer();
        int port = masterServerConfig.getClientListenerPort();
        try (Client client = createClient("localhost", port)) {
            for (int test = 0; test < tests; test++) {
                Future<NodeResponse> future = client.executeTask(createDummyRequest());
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
        print("dummy task takes " +
                (timer.getTimePassed() / (double) tests) + "mls");
    }


    private static Client createClient(String host, int port) {
        return new Client(host, port);
    }

    private static NodeRequest createDummyRequest() {
        return NodeRequest.regular(UUID.randomUUID(), TASK_NAME, new HashMap<>());
    }
}
