package ru.babobka.primecounter.task;

import ru.babobka.nodetask.model.TaskFactory;
import ru.babobka.nodeutils.container.AbstractApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.primecounter.PrimeCounterServiceApplicationContainer;
import ru.babobka.primecounter.model.PrimeCounterDataValidators;
import ru.babobka.primecounter.model.PrimeCounterDistributor;
import ru.babobka.primecounter.model.PrimeCounterReducer;
import ru.babobka.primecounter.service.PrimeCounterServiceFactory;

/**
 * Created by 123 on 12.10.2017.
 */
public class PrimeCounterTaskFactory extends TaskFactory<PrimeCounterTask> {
    public PrimeCounterTaskFactory() {
        super(PrimeCounterTask.class);
    }

    @Override
    public PrimeCounterTask createTask() {
        return new PrimeCounterTask();
    }

    @Override
    public AbstractApplicationContainer getApplicationContainer() {
        return new PrimeCounterTaskFactoryApplicationContainer();
    }

    private static class PrimeCounterTaskFactoryApplicationContainer extends AbstractApplicationContainer {

        @Override
        protected void containImpl(Container container) {
            container.put(new PrimeCounterServiceApplicationContainer());
            container.put(new PrimeCounterDataValidators());
            container.put(new PrimeCounterReducer());
            container.put(new PrimeCounterDistributor());
            container.put(new PrimeCounterServiceFactory());
        }
    }
}
