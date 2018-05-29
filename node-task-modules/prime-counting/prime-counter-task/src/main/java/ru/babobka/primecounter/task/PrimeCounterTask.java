package ru.babobka.primecounter.task;

import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodetask.model.*;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.primecounter.model.PrimeCounterDataValidators;
import ru.babobka.primecounter.model.PrimeCounterDistributor;
import ru.babobka.primecounter.model.PrimeCounterReducer;
import ru.babobka.primecounter.model.PrimeCounterTaskExecutor;


/**
 * Created by dolgopolov.a on 15.12.15.
 */
public class PrimeCounterTask extends SubTask {
    private static final String DESCRIPTION = "Counts prime numbers in a given range";
    private static final Long MIN_RANGE_TO_PARALLEL = 5000L;
    private final PrimeCounterReducer reducer = Container.getInstance().get(PrimeCounterReducer.class);
    private final PrimeCounterDistributor distributor = Container.getInstance().get(PrimeCounterDistributor.class);
    private final PrimeCounterDataValidators primeCounterDataValidators = Container.getInstance().get(PrimeCounterDataValidators.class);
    private final PrimeCounterTaskExecutor primeCounterTaskExecutor = new PrimeCounterTaskExecutor();

    PrimeCounterTask() {
    }

    @Override
    public RequestDistributor getDistributor() {
        return distributor;
    }

    @Override
    public Reducer getReducer() {
        return reducer;
    }

    @Override
    public TaskExecutor getTaskExecutor() {
        return primeCounterTaskExecutor;
    }

    @Override
    public DataValidators getDataValidators() {
        return primeCounterDataValidators;
    }

    @Override
    public boolean isRequestDataTooSmall(NodeRequest request) {
        long begin = request.getDataValue(Params.BEGIN.getValue());
        long end = request.getDataValue((Params.END.getValue()));
        return (end - begin) <= MIN_RANGE_TO_PARALLEL;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    public boolean isRaceStyle() {
        return false;
    }

}
