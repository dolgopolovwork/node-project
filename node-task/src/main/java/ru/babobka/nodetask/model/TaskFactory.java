package ru.babobka.nodetask.model;

import ru.babobka.nodeutils.container.ApplicationContainer;
import ru.babobka.nodeutils.container.Container;

/**
 * Created by 123 on 12.10.2017.
 */
public abstract class TaskFactory<T extends SubTask> {

    private static final ApplicationContainer DUMMY_CONTAINER = new ApplicationContainer() {
        @Override
        public void contain(Container container) {

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

    public ApplicationContainer getApplicationContainer() {
        return DUMMY_CONTAINER;
    }

    public Class<T> getTaskType() {
        return type;
    }

}
