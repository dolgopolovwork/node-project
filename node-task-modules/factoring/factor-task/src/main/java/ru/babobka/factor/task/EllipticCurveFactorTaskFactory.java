package ru.babobka.factor.task;

import ru.babobka.factor.FactorServiceApplicationContainer;
import ru.babobka.factor.model.EllipticFactorDataValidators;
import ru.babobka.factor.model.EllipticFactorDistributor;
import ru.babobka.factor.model.EllipticFactorReducer;
import ru.babobka.factor.service.EllipticCurveFactorServiceFactory;
import ru.babobka.nodetask.model.TaskFactory;
import ru.babobka.nodeutils.container.AbstractApplicationContainer;
import ru.babobka.nodeutils.container.Container;

/**
 * Created by 123 on 12.10.2017.
 */
public class EllipticCurveFactorTaskFactory extends TaskFactory<EllipticCurveFactorTask> {

    public EllipticCurveFactorTaskFactory() {
        super(EllipticCurveFactorTask.class);
    }

    @Override
    public EllipticCurveFactorTask createTask() {
        return new EllipticCurveFactorTask();
    }

    @Override
    public AbstractApplicationContainer getApplicationContainer() {
        return new EllipticCurveFactorTaskApplicationContainer();
    }

    private static class EllipticCurveFactorTaskApplicationContainer extends AbstractApplicationContainer {

        @Override
        protected void containImpl(Container container) {
            container.put(new FactorServiceApplicationContainer());
            container.put(new EllipticCurveFactorServiceFactory());
            container.put(new EllipticFactorDataValidators());
            container.put(new EllipticFactorDistributor());
            container.put(new EllipticFactorReducer());
        }
    }
}
