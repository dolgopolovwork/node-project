package ru.babobka.dlp.service.regular;

import ru.babobka.dlp.service.pollard.parallel.regular.ParallelPollardDlpService;
import ru.babobka.nodeutils.container.Properties;
import ru.babobka.nodeutils.key.UtilKey;

/**
 * Created by 123 on 09.11.2017.
 */
public class PollardDlpServiceFactory {
    public ParallelPollardDlpService get() {
        return new ParallelPollardDlpService(
                Properties.getInt(UtilKey.SERVICE_THREADS_NUM,
                        Runtime.getRuntime().availableProcessors()));
    }
}
