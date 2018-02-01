package ru.babobka.dlp.service;

import ru.babobka.dlp.service.pollard.parallel.ParallelPollardDlpService;
import ru.babobka.nodeutils.container.Container;

/**
 * Created by 123 on 09.11.2017.
 */
public class PollardDlpServiceFactory {
    public ParallelPollardDlpService get() {
        return new ParallelPollardDlpService(Container.getInstance().get("service-threads"));
    }
}
