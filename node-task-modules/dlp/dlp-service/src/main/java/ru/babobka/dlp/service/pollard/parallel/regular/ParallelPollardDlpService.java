package ru.babobka.dlp.service.pollard.parallel.regular;

import ru.babobka.dlp.model.dist.DlpTaskDist;
import ru.babobka.dlp.model.regular.DlpTask;
import ru.babobka.dlp.service.pollard.parallel.dist.ParallelDistributedPollardDlpService;
import ru.babobka.nodeutils.thread.ThreadPoolService;

import java.math.BigInteger;

/**
 * Created by 123 on 16.01.2018.
 */
public class ParallelPollardDlpService extends ThreadPoolService<DlpTask, BigInteger> {

    private final ParallelDistributedPollardDlpService parallelDistributedPollardDlpService;

    public ParallelPollardDlpService(int cores) {
        super(cores);
        parallelDistributedPollardDlpService = new ParallelDistributedPollardDlpService(cores);
    }

    @Override
    protected void stopImpl() {
        parallelDistributedPollardDlpService.stopImpl();
    }

    @Override
    protected BigInteger getStoppedResponse() {
        return BigInteger.ONE;
    }

    @Override
    protected BigInteger executeImpl(DlpTask task) {
        DlpTaskDist dlpTaskDist = new DlpTaskDist(task.getGen(), task.getY(), Integer.MAX_VALUE);
        return parallelDistributedPollardDlpService.executeImpl(dlpTaskDist).getExp();
    }


}
