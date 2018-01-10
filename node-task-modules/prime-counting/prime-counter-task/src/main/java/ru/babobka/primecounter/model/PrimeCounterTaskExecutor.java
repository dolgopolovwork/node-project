package ru.babobka.primecounter.model;

import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodetask.model.ExecutionResult;
import ru.babobka.nodetask.model.TaskExecutor;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.primecounter.service.PrimeCounterServiceFactory;
import ru.babobka.primecounter.service.PrimeCounterTaskService;
import ru.babobka.primecounter.task.Params;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 123 on 12.10.2017.
 */
public class PrimeCounterTaskExecutor extends TaskExecutor {
    private final PrimeCounterServiceFactory primeCounterServiceFactory = Container.getInstance().get(PrimeCounterServiceFactory.class);
    private final PrimeCounterTaskService primeCounterTaskService = primeCounterServiceFactory.get();

    @Override
    protected ExecutionResult executeImpl(NodeRequest request) {
        Map<String, Serializable> result = new HashMap<>();
        long begin = Long.parseLong(request.getStringDataValue(Params.BEGIN.getValue()));
        long end = Long.parseLong(request.getStringDataValue(Params.END.getValue()));
        Range range = new Range(begin, end);
        int primes = primeCounterTaskService.execute(range);
        result.put(Params.PRIME_COUNT.getValue(), primes);
        return new ExecutionResult(primeCounterTaskService.isStopped(), result);
    }

    @Override
    public void stopCurrentTask() {
        primeCounterTaskService.stop();
    }
}
