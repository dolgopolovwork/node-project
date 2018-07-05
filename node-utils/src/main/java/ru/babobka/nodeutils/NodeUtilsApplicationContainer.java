package ru.babobka.nodeutils;

import ru.babobka.nodeutils.container.AbstractApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.util.StreamUtil;

/**
 * Created by 123 on 04.11.2017.
 */
public class NodeUtilsApplicationContainer extends AbstractApplicationContainer {
    @Override
    protected void containImpl(Container container) {
        container.put(new StreamUtil());
    }
}
