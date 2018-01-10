package ru.babobka.primecounter.service;

/**
 * Created by 123 on 09.11.2017.
 */
public class PrimeCounterServiceFactory {
    public PrimeCounterTaskService get() {
        return new PrimeCounterTaskService(Runtime.getRuntime().availableProcessors());
    }
}
