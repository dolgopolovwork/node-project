package ru.babobka.dummy;

import ru.babobka.nodetask.model.TaskFactory;
import ru.babobka.nodeutils.container.ApplicationContainer;
import ru.babobka.nodeutils.container.Container;

/**
 * Created by 123 on 12.10.2017.
 */
public class DummyTaskFactory extends TaskFactory<DummyTask> {
    public DummyTaskFactory() {
        super(DummyTask.class);
    }

    @Override
    public DummyTask createTask() {
        return new DummyTask();
    }

    @Override
    public ApplicationContainer getApplicationContainer() {
        return new PrimeCounterTaskFactoryApplicationContainer();
    }

    private static class PrimeCounterTaskFactoryApplicationContainer implements ApplicationContainer {

        @Override
        public void contain(Container container) {

        }
    }
}
