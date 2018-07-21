package ru.babobka.dlp.task.dist;

import ru.babobka.dlp.mapper.PollardDistResultMapper;
import ru.babobka.dlp.model.dist.DlpTaskDist;
import ru.babobka.dlp.model.dist.PollardDistResult;
import ru.babobka.dlp.service.dist.PollardDlpDistServiceFactory;
import ru.babobka.dlp.service.pollard.parallel.dist.ParallelDistributedPollardDlpService;
import ru.babobka.dlp.task.Params;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.data.Data;
import ru.babobka.nodetask.model.ExecutionResult;
import ru.babobka.nodetask.model.TaskExecutor;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.math.Fp;

import java.math.BigInteger;

/**
 * Created by 123 on 09.07.2018.
 */
public class PollardDlpDistTaskExecutor extends TaskExecutor {
    private final PollardDlpDistServiceFactory pollardDlpServiceFactory = Container.getInstance().get(PollardDlpDistServiceFactory.class);
    private final ParallelDistributedPollardDlpService dlpService = pollardDlpServiceFactory.get();
    private final PollardDistResultMapper pollardDistResultMapper = Container.getInstance().get(PollardDistResultMapper.class);

    @Override
    protected ExecutionResult executeImpl(NodeRequest request) {
        try {
            Data data = new Data();
            BigInteger x = request.getDataValue(Params.X.getValue());
            BigInteger y = request.getDataValue(Params.Y.getValue());
            BigInteger mod = request.getDataValue(Params.MOD.getValue());
            int loops = request.getDataValue(Params.LOOPS.getValue());
            DlpTaskDist dlpTask = new DlpTaskDist(new Fp(x, mod), new Fp(y, mod), loops);
            PollardDistResult pollardDistResult = dlpService.execute(dlpTask);
            data.put(Params.X.getValue(), x);
            data.put(Params.Y.getValue(), y);
            data.put(Params.MOD.getValue(), mod);
            if (pollardDistResult.hasResult()) {
                data.put(Params.EXP.getValue(), pollardDistResult.getExp());
            } else {
                data.put(pollardDistResultMapper.map(pollardDistResult));
            }
            data.put(pollardDistResultMapper.map(pollardDistResult));
            return new ExecutionResult(dlpService.isStopped(), data);
        } finally {
            dlpService.stop();
        }
    }

    @Override
    public void stopCurrentTask() {
        dlpService.stop();
    }
}
