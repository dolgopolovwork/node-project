package ru.babobka.nodeutils;

import ru.babobka.nodeutils.container.AbstractApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.time.TimerInvoker;
import ru.babobka.nodeutils.util.StreamUtil;

/**
 * Created by 123 on 04.11.2017.
 */
public class NodeUtilsApplicationContainer extends AbstractApplicationContainer {
    private static final int MAX_DELAY_MILLIS = 1_000;

    @Override
    protected void containImpl(Container container) {
        container.put(new StreamUtil());
        container.put(TimerInvoker.create(MAX_DELAY_MILLIS));
    }
}
