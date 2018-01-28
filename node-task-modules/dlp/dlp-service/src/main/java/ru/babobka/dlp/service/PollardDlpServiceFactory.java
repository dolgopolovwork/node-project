package ru.babobka.dlp.service;

import ru.babobka.dlp.service.pollard.parallel.ParallelPollardDlpService;

/**
 * Created by 123 on 09.11.2017.
 */
public class PollardDlpServiceFactory {
    public ParallelPollardDlpService get() {
        return get(Runtime.getRuntime().availableProcessors());
    }

    public ParallelPollardDlpService get(int cores) {
        return new ParallelPollardDlpService(cores);
    }
}
