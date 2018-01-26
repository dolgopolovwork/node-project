package ru.babobka.dlp.pollard.parallel;

import ru.babobka.dlp.DlpService;
import ru.babobka.dlp.model.DlpTask;

import java.math.BigInteger;

/**
 * Created by 123 on 26.01.2018.
 */
public class ParallelPollardDLPServiceTestable extends DlpService {

    private final ParallelPollardDLPService parallelPollardDLPService;

    public ParallelPollardDLPServiceTestable() {
        this.parallelPollardDLPService = new ParallelPollardDLPService(Runtime.getRuntime().availableProcessors());
    }

    @Override
    protected BigInteger dlpImpl(DlpTask task) {
        return parallelPollardDLPService.executeNoShutDown(task);
    }

    public void stop() {
        parallelPollardDLPService.stop();
    }

    public void reset() {
        parallelPollardDLPService.setDone(false);
    }
}
