package ru.babobka.primecounter.model;

import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.data.Data;
import ru.babobka.nodetask.model.ExecutionResult;
import ru.babobka.nodetask.model.TaskExecutor;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.primecounter.service.PrimeCounterServiceFactory;
import ru.babobka.primecounter.service.PrimeCounterTaskService;
import ru.babobka.primecounter.task.Params;

/**
 * Created by 123 on 12.10.2017.
 */
public class PrimeCounterTaskExecutor extends TaskExecutor {
    private final PrimeCounterServiceFactory primeCounterServiceFactory = Container.getInstance().get(PrimeCounterServiceFactory.class);
    private final PrimeCounterTaskService primeCounterTaskService = primeCounterServiceFactory.get();

    @Override
    protected ExecutionResult executeImpl(NodeRequest request) {
        try {
            Data result = new Data();
            long begin = request.getDataValue(Params.BEGIN.getValue());
            long end = request.getDataValue(Params.END.getValue());
            Range range = new Range(begin, end);
            int primes = primeCounterTaskService.execute(range);
            result.put(Params.PRIME_COUNT.getValue(), primes);
            return new ExecutionResult(primeCounterTaskService.isStopped(), result);
        } finally {
            primeCounterTaskService.stop();
        }
    }

    @Override
    public void stopCurrentTask() {
        primeCounterTaskService.stop();
    }
}
