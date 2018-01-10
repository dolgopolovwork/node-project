package ru.babobka.factor;

import ru.babobka.factor.model.ec.multprovider.FastMultiplicationProvider;
import ru.babobka.nodeutils.container.ApplicationContainer;
import ru.babobka.nodeutils.container.Container;

/**
 * Created by 123 on 29.10.2017.
 */
public class FactorServiceApplicationContainer implements ApplicationContainer {

    @Override
    public void contain(Container container) {
        container.put(new FastMultiplicationProvider());
    }
}
