package ru.babobka.factor.task;

import ru.babobka.factor.model.FactoringResult;
import ru.babobka.factor.service.EllipticCurveFactorService;
import ru.babobka.factor.service.EllipticCurveFactorServiceFactory;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.data.Data;
import ru.babobka.nodetask.model.ExecutionResult;
import ru.babobka.nodetask.model.TaskExecutor;
import ru.babobka.nodeutils.container.Container;

import java.math.BigInteger;

/**
 * Created by 123 on 20.06.2017.
 */
public class EllipticCurveFactorTaskExecutor extends TaskExecutor {
    private final EllipticCurveFactorServiceFactory ellipticCurveFactorServiceFactory = Container.getInstance().get(EllipticCurveFactorServiceFactory.class);
    private final EllipticCurveFactorService factorService = ellipticCurveFactorServiceFactory.get();

    @Override
    protected ExecutionResult executeImpl(NodeRequest request) {
        try {
            Data data = new Data();
            BigInteger number = request.getDataValue(Params.NUMBER.getValue());
            FactoringResult factoringResult = factorService.execute(number);
            if (factoringResult != null) {
                data.put(Params.NUMBER.getValue(), number);
                data.put(Params.FACTOR.getValue(), factoringResult.getFactor());
                return new ExecutionResult(factorService.isStopped(), data);
            }
            return ExecutionResult.stopped();
        } finally {
            factorService.stop();
        }
    }

    @Override
    public void stopCurrentTask() {
        factorService.stop();
    }
}
