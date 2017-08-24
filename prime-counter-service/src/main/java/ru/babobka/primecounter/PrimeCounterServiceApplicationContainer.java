package ru.babobka.primecounter;

import ru.babobka.nodeutils.container.ApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.primecounter.tester.DummyPrimeTester;

/**
 * Created by 123 on 04.11.2017.
 */
public class PrimeCounterServiceApplicationContainer implements ApplicationContainer {
    @Override
    public void contain(Container container) {
        container.put(new DummyPrimeTester());
    }
}
