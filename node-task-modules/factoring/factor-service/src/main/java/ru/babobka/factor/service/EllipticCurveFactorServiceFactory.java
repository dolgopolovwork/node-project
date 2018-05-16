package ru.babobka.factor.service;

import ru.babobka.nodeutils.container.Properties;
import ru.babobka.nodeutils.key.UtilKey;

/**
 * Created by 123 on 09.11.2017.
 */
public class EllipticCurveFactorServiceFactory {
    public EllipticCurveFactorService get() {
        return new EllipticCurveFactorService(
                Properties.getInt(UtilKey.SERVICE_THREADS_NUM,
                        Runtime.getRuntime().availableProcessors()));
    }
}
