package ru.babobka.primecounter.service;

import ru.babobka.nodeutils.container.Properties;

/**
 * Created by 123 on 09.11.2017.
 */
public class PrimeCounterServiceFactory {
    public PrimeCounterTaskService get() {
        return new PrimeCounterTaskService(Properties.getInt("service-threads", Runtime.getRuntime().availableProcessors()));
    }
}
