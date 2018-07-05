package ru.babobka.factor;

import ru.babobka.factor.model.ec.multprovider.FastMultiplicationProvider;
import ru.babobka.nodeutils.container.AbstractApplicationContainer;
import ru.babobka.nodeutils.container.Container;

/**
 * Created by 123 on 29.10.2017.
 */
public class FactorServiceApplicationContainer extends AbstractApplicationContainer {

    @Override
    protected void containImpl(Container container) {
        container.put(new FastMultiplicationProvider());
    }
}
