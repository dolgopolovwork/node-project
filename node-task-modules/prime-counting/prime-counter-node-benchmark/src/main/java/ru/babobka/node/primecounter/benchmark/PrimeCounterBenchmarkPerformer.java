package ru.babobka.node.primecounter.benchmark;

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
 * Created by 123 on 29.07.2018.
 */
public class PrimeCounterBenchmarkPerformer extends ClientBenchmarkPerformer {
    private static final String TASK_NAME = "ru.babobka.primecounter.task.PrimeCounterTask";
    private final int begin;
    private final int end;

    public PrimeCounterBenchmarkPerformer(int begin, int end) {
        this.begin = begin;
        this.end = end;
    }

    private static NodeRequest createPrimeCounterRequest(long begin, long end) {
        Data data = new Data();
        data.put("begin", begin);
        data.put("end", end);
        return NodeRequest.regular(UUID.randomUUID(), TASK_NAME, data);
    }

    @Override
    protected void performBenchmark(Client client, AtomicLong timer) throws ExecutionException, InterruptedException, IOException {
        Future<NodeResponse> future = client.executeTask(createPrimeCounterRequest(begin, end));
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
