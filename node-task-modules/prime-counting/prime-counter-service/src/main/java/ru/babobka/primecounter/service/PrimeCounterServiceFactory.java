package ru.babobka.primecounter.service;

import ru.babobka.nodeutils.container.Properties;
import ru.babobka.nodeutils.key.UtilKey;

/**
 * Created by 123 on 09.11.2017.
 */
public class PrimeCounterServiceFactory {
    public PrimeCounterTaskService get() {
        return new PrimeCounterTaskService(Properties.getInt(
                UtilKey.SERVICE_THREADS_NUM,
                Runtime.getRuntime().availableProcessors()));
    }
}
