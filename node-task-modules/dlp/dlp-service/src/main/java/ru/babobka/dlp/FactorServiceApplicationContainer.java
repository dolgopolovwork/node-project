package ru.babobka.dlp;

import ru.babobka.dlp.pollard.PollardCollisionService;
import ru.babobka.dlp.pollard.parallel.PrimeDistinguishable;
import ru.babobka.nodeutils.container.ApplicationContainer;
import ru.babobka.nodeutils.container.Container;

/**
 * Created by 123 on 29.10.2017.
 */
public class FactorServiceApplicationContainer implements ApplicationContainer {

    @Override
    public void contain(Container container) {
        container.put(new PollardCollisionService());
        container.put(new PrimeDistinguishable());
    }
}
