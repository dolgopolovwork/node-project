package ru.babobka.nodetask.model;

import ru.babobka.nodeutils.container.AbstractApplicationContainer;
import ru.babobka.nodeutils.container.Container;

/**
 * Created by 123 on 12.10.2017.
 */
public abstract class TaskFactory<T extends SubTask> {

    private static final AbstractApplicationContainer DUMMY_CONTAINER = new AbstractApplicationContainer() {
        @Override
        protected void containImpl(Container container) throws Exception {
            //do nothing. it's ok
        }
    };

    private final Class<T> type;

    public TaskFactory(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("type is null");
        }
        this.type = type;
    }

    public abstract T createTask();

    public AbstractApplicationContainer getApplicationContainer() {
        return DUMMY_CONTAINER;
    }

    public Class<T> getTaskType() {
        return type;
    }

}
