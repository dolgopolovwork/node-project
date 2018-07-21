package ru.babobka.node.dummybenchmark;

import ru.babobka.nodeclient.Client;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.data.Data;
import ru.babobka.nodeserials.enumerations.ResponseStatus;
import ru.babobka.nodetester.benchmark.performer.ClientBenchmarkPerformer;
import ru.babobka.nodeutils.time.Timer;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

import static ru.babobka.nodeclient.console.CLI.printErr;

/**
 * Created by 123 on 01.02.2018.
 */
public class DummyNodeBenchmarkPerformer extends ClientBenchmarkPerformer {

    private static final String TASK_NAME = "ru.babobka.dummy.DummyTask";

    private static NodeRequest createDummyRequest() {
        return NodeRequest.regular(UUID.randomUUID(), TASK_NAME, new Data());
    }

    @Override
    protected void performBenchmark(Client client, AtomicLong timer) throws IOException, ExecutionException, InterruptedException {
        Future<NodeResponse> future = client.executeTask(createDummyRequest());
        Timer requestTimer = new Timer();
        NodeResponse response = future.get();
        timer.addAndGet(requestTimer.getTimePassed());
        if (response.getStatus() != ResponseStatus.NORMAL) {
            String message = "cannot get the result. response is " + response;
            printErr(message);
            throw new IOException(message);
        }
    }
}
