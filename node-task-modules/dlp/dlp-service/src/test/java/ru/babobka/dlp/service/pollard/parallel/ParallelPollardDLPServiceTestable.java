package ru.babobka.dlp.service.pollard.parallel;

import ru.babobka.dlp.service.DlpService;
import ru.babobka.dlp.model.DlpTask;

import java.math.BigInteger;

/**
 * Created by 123 on 26.01.2018.
 */
public class ParallelPollardDLPServiceTestable extends DlpService {

    private final ParallelPollardDlpService parallelPollardDlpService;

    public ParallelPollardDLPServiceTestable() {
        this.parallelPollardDlpService = new ParallelPollardDlpService(Runtime.getRuntime().availableProcessors());
    }

    @Override
    protected BigInteger dlpImpl(DlpTask task) {
        return parallelPollardDlpService.executeNoShutDown(task);
    }

    public void stop() {
        parallelPollardDlpService.stop();
    }

    public void reset() {
        parallelPollardDlpService.setDone(false);
    }
}
