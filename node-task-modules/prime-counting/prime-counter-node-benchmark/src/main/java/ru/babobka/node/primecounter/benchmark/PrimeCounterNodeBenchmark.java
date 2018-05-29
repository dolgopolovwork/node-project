package ru.babobka.node.primecounter.benchmark;

import ru.babobka.nodeclient.Client;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.data.Data;
import ru.babobka.nodeserials.enumerations.ResponseStatus;
import ru.babobka.nodetester.benchmark.NodeBenchmark;
import ru.babobka.nodeutils.time.Timer;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

import static ru.babobka.nodeclient.CLI.printErr;

/**
 * Created by 123 on 01.02.2018.
 */
public class PrimeCounterNodeBenchmark extends NodeBenchmark {

    private static final String TASK_NAME = "ru.babobka.primecounter.task.PrimeCounterTask";
    private final int begin;
    private final int end;

    public PrimeCounterNodeBenchmark(String appName, int tests, int begin, int end) {
        super(appName, tests);
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
    protected String getDescription() {
        return "prime counting in range [" + begin + ";" + end + "]";
    }

    @Override
    protected void onBenchmark(Client client, AtomicLong timerStorage) throws IOException, ExecutionException, InterruptedException {
        Future<NodeResponse> future = client.executeTask(createPrimeCounterRequest(begin, end));
        Timer requestTimer = new Timer();
        NodeResponse response = future.get();
        timerStorage.addAndGet(requestTimer.getTimePassed());
        if (response.getStatus() != ResponseStatus.NORMAL) {
            String message = "cannot get the result. response is " + response;
            printErr(message);
            throw new IOException(message);
        }
    }
}
