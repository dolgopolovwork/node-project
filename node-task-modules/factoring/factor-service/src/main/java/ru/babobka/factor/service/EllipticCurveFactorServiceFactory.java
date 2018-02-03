package ru.babobka.factor.service;

import ru.babobka.nodeutils.container.Container;

/**
 * Created by 123 on 09.11.2017.
 */
public class EllipticCurveFactorServiceFactory {
    public EllipticCurveFactorService get() {
        return new EllipticCurveFactorService(Container.getInstance().get("service-threads", Runtime.getRuntime().availableProcessors()));
    }
}
