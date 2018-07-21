package ru.babobka.dlp.task.regular;

import ru.babobka.dlp.model.regular.DlpTask;
import ru.babobka.dlp.service.regular.PollardDlpServiceFactory;
import ru.babobka.dlp.service.pollard.parallel.regular.ParallelPollardDlpService;
import ru.babobka.dlp.task.Params;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.data.Data;
import ru.babobka.nodetask.model.ExecutionResult;
import ru.babobka.nodetask.model.TaskExecutor;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.math.Fp;

import java.math.BigInteger;

/**
 * Created by 123 on 20.06.2017.
 */
public class PollardDlpTaskExecutor extends TaskExecutor {
    private final PollardDlpServiceFactory pollardDlpServiceFactory = Container.getInstance().get(PollardDlpServiceFactory.class);
    private final ParallelPollardDlpService dlpService = pollardDlpServiceFactory.get();

    @Override
    protected ExecutionResult executeImpl(NodeRequest request) {
        try {
            Data data = new Data();
            BigInteger x = request.getDataValue(Params.X.getValue());
            BigInteger y = request.getDataValue(Params.Y.getValue());
            BigInteger mod = request.getDataValue(Params.MOD.getValue());
            DlpTask dlpTask = new DlpTask(new Fp(x, mod), new Fp(y, mod));
            BigInteger exp = dlpService.execute(dlpTask);
            if (exp != null) {
                data.put(Params.X.getValue(), x);
                data.put(Params.Y.getValue(), y);
                data.put(Params.MOD.getValue(), mod);
                data.put(Params.EXP.getValue(), exp);
                return new ExecutionResult(dlpService.isStopped(), data);
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
