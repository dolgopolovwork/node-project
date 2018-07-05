package ru.babobka.nodetask;

import ru.babobka.nodetask.util.TasksUtil;
import ru.babobka.nodeutils.container.AbstractApplicationContainer;
import ru.babobka.nodeutils.container.Container;

/**
 * Created by 123 on 04.11.2017.
 */
public class NodeTaskApplicationContainer extends AbstractApplicationContainer {
    @Override
    protected void containImpl(Container container) {
        container.put(new TasksUtil());
    }
}
