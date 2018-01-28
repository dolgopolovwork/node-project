package ru.babobka.dlp.task;

import ru.babobka.dlp.model.DlpTask;
import ru.babobka.dlp.service.PollardDlpServiceFactory;
import ru.babobka.dlp.service.pollard.parallel.ParallelPollardDlpService;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodetask.model.ExecutionResult;
import ru.babobka.nodetask.model.TaskExecutor;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.math.Fp;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 123 on 20.06.2017.
 */
public class PollardDlpTaskExecutor extends TaskExecutor {
    private final PollardDlpServiceFactory pollardDlpServiceFactory = Container.getInstance().get(PollardDlpServiceFactory.class);
    private final ParallelPollardDlpService dlpService = pollardDlpServiceFactory.get();

    @Override
    protected ExecutionResult executeImpl(NodeRequest request) {
        try {
            Map<String, Serializable> result = new HashMap<>();
            BigInteger x = request.getDataValue(Params.X.getValue());
            BigInteger y = request.getDataValue(Params.Y.getValue());
            BigInteger mod = request.getDataValue(Params.MOD.getValue());
            DlpTask dlpTask = new DlpTask(new Fp(x, mod), new Fp(y, mod));
            BigInteger exp = dlpService.execute(dlpTask);
            if (exp != null) {
                result.put(Params.X.getValue(), x);
                result.put(Params.Y.getValue(), y);
                result.put(Params.MOD.getValue(), mod);
                result.put(Params.EXP.getValue(), exp);
                return new ExecutionResult(dlpService.isStopped(), result);
            }
            return ExecutionResult.stopped();
        } finally {
            dlpService.stop();
        }
    }

    @Override
    public void stopCurrentTask() {
        dlpService.stop();
    }
}
