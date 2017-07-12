package ru.babobka.primecounter.task;

import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.primecounter.model.PrimeCounterDistributor;
import ru.babobka.primecounter.model.PrimeCounterReducer;


import ru.babobka.subtask.model.*;


/**
 * Created by dolgopolov.a on 15.12.15.
 */
public class PrimeCounterTask extends SubTask {


    private static final Long MIN_RANGE_TO_PARALLEL = 5000L;

    private final PrimeCounterReducer reducer = new PrimeCounterReducer();

    private final PrimeCounterDistributor distributor = new PrimeCounterDistributor(NAME);

    private final PrimeCounterRequestValidator primeCounterRequestValidator = new PrimeCounterRequestValidator();

    private final PrimeCounterTaskExecutor primeCounterTaskExecutor = new PrimeCounterTaskExecutor();

    private static final String NAME = "Dummy prime counter";

    private static final String DESCRIPTION = "Counts prime numbers in a given range";


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
    public RequestValidator getRequestValidator() {
        return primeCounterRequestValidator;
    }

    @Override
    public boolean isRequestDataTooSmall(NodeRequest request) {
        long begin = Long.parseLong(request.getStringDataValue(Params.BEGIN.getValue()));
        long end = Long.parseLong(request.getStringDataValue(Params.END.getValue()));
        return (end - begin) <= MIN_RANGE_TO_PARALLEL;
    }

    public SubTask newInstance() {
        return new PrimeCounterTask();
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean isRaceStyle() {
        return false;
    }

}
