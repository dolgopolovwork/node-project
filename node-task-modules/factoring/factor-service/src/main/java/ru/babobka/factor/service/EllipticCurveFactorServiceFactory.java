package ru.babobka.factor.service;

/**
 * Created by 123 on 09.11.2017.
 */
public class EllipticCurveFactorServiceFactory {
    public EllipticCurveFactorService get() {
        return get(Runtime.getRuntime().availableProcessors());
    }

    public EllipticCurveFactorService get(int cores) {
        return new EllipticCurveFactorService(cores);
    }
}
