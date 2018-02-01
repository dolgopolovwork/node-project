package ru.babobka.primecounter.service;

import ru.babobka.nodeutils.container.Container;

/**
 * Created by 123 on 09.11.2017.
 */
public class PrimeCounterServiceFactory {
    public PrimeCounterTaskService get() {
        return new PrimeCounterTaskService(Container.getInstance().get("service-threads"));
    }
}
