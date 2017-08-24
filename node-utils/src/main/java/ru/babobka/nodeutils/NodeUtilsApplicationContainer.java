package ru.babobka.nodeutils;

import ru.babobka.nodeutils.container.ApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.util.StreamUtil;

/**
 * Created by 123 on 04.11.2017.
 */
public class NodeUtilsApplicationContainer implements ApplicationContainer {
    @Override
    public void contain(Container container) {
        container.put(new StreamUtil());
    }
}
