package ru.babobka.node.dummybenchmark;

import ru.babobka.nodeclient.Client;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.data.Data;
import ru.babobka.nodeserials.enumerations.ResponseStatus;
import ru.babobka.nodetester.benchmark.NodeBenchmark;
import ru.babobka.nodeutils.time.Timer;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

import static ru.babobka.nodeclient.CLI.printErr;

/**
 * Created by 123 on 01.02.2018.
 */
public class DummyNodeBenchmark extends NodeBenchmark {

    private static final String TASK_NAME = "ru.babobka.dummy.DummyTask";

    public DummyNodeBenchmark(String appName, int tests) {
        super(appName, tests);
    }

    @Override
    protected String getDescription() {
        return "dummy benchmark";
    }

    @Override
    protected void onBenchmark(Client client, AtomicLong timerStorage) throws IOException, ExecutionException, InterruptedException {
        Future<NodeResponse> future = client.executeTask(createDummyRequest());
        Timer requestTimer = new Timer();
        NodeResponse response = future.get();
        timerStorage.addAndGet(requestTimer.getTimePassed());
        if (response.getStatus() != ResponseStatus.NORMAL) {
            String message = "cannot get the result. response is " + response;
            printErr(message);
            throw new IOException(message);
        }
    }

    private static NodeRequest createDummyRequest() {
        return NodeRequest.regular(UUID.randomUUID(), TASK_NAME, new Data());
    }
}
