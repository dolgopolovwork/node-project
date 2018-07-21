package ru.babobka.dlp.service.dist;

import ru.babobka.dlp.service.pollard.parallel.dist.ParallelDistributedPollardDlpService;
import ru.babobka.nodeutils.container.Properties;
import ru.babobka.nodeutils.key.UtilKey;

/**
 * Created by 123 on 09.07.2018.
 */
public class PollardDlpDistServiceFactory {
    public ParallelDistributedPollardDlpService get() {
        return new ParallelDistributedPollardDlpService(
                Properties.getInt(UtilKey.SERVICE_THREADS_NUM,
                        Runtime.getRuntime().availableProcessors()));
    }
}
