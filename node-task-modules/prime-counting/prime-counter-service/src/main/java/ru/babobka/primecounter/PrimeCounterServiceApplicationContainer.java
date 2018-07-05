package ru.babobka.primecounter;

import ru.babobka.nodeutils.container.AbstractApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.primecounter.tester.DummyPrimeTester;

/**
 * Created by 123 on 04.11.2017.
 */
public class PrimeCounterServiceApplicationContainer extends AbstractApplicationContainer {
    @Override
    protected void containImpl(Container container) {
        container.put(new DummyPrimeTester());
    }
}
