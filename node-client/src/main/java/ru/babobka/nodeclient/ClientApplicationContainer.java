package ru.babobka.nodeclient;

import ru.babobka.nodeutils.container.AbstractApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.time.TimerInvoker;
import ru.babobka.nodeutils.util.StreamUtil;

/**
 * Created by 123 on 16.12.2017.
 */
public class ClientApplicationContainer extends AbstractApplicationContainer {

    @Override
    protected void containImpl(Container container) {
        container.put(new StreamUtil());
        container.put(TimerInvoker.createMaxOneSecondDelay());
    }
}
