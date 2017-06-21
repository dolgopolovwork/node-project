package ru.babobka.factor.task;

import ru.babobka.factor.model.FactoringResult;
import ru.babobka.factor.service.EllipticCurveFactorService;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.subtask.model.ExecutionResult;
import ru.babobka.subtask.model.TaskExecutor;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 123 on 20.06.2017.
 */
public class EllipticCurveFactorTaskExecutor extends TaskExecutor {

    private final EllipticCurveFactorService factorService = new EllipticCurveFactorService();

    @Override
    public ExecutionResult execute(int threads, NodeRequest request) {

        try {
            Map<String, Serializable> result = new HashMap<>();
            BigInteger number = new BigInteger(request.getStringDataValue(Params.NUMBER.getValue()));
            FactoringResult factoringResult = factorService.factor(number,
                    Math.min(threads, Runtime.getRuntime().availableProcessors()));
            if (factoringResult != null) {
                result.put(Params.NUMBER.getValue(), number);
                result.put(Params.FACTOR.getValue(), factoringResult.getFactor());
                result.put(Params.X.getValue(), factoringResult.getEllipticCurveProjective().getX());
                result.put(Params.Y.getValue(), factoringResult.getEllipticCurveProjective().getY());
                result.put(Params.A.getValue(), factoringResult.getEllipticCurveProjective().getA());
                result.put(Params.B.getValue(), factoringResult.getEllipticCurveProjective().getB());
                return new ExecutionResult(factorService.isStopped(), result);
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
