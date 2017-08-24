package ru.babobka.nodetask;

import ru.babobka.nodetask.util.TasksUtil;
import ru.babobka.nodeutils.container.ApplicationContainer;
import ru.babobka.nodeutils.container.Container;

/**
 * Created by 123 on 04.11.2017.
 */
public class NodeTaskApplicationContainer implements ApplicationContainer {
    @Override
    public void contain(Container container) {
        container.put(new TasksUtil());
    }
}
