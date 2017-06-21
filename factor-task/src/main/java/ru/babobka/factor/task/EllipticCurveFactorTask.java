package ru.babobka.factor.task;

import java.math.BigInteger;

import ru.babobka.factor.model.EllipticFactorDistributor;
import ru.babobka.factor.model.EllipticFactorReducer;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.subtask.model.*;

/**
 * Created by dolgopolov.a on 08.12.15.
 */
public class EllipticCurveFactorTask extends SubTask {

    private final EllipticFactorDistributor distributor = new EllipticFactorDistributor(NAME);

    private final EllipticFactorReducer reducer = new EllipticFactorReducer();

    private final EllipticCurveFactorTaskExecutor taskExecutor = new EllipticCurveFactorTaskExecutor();

    private final EllipticCurveRequestValidator requestValidator = new EllipticCurveRequestValidator();

    private static final String NAME = "Elliptic curve factor";

    private static final String DESCRIPTION = "Factorizes a given big composite number using Lenstra algorithm";


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
        return taskExecutor;
    }

    @Override
    public RequestValidator getRequestValidator() {
        return requestValidator;
    }

    @Override
    public boolean isRequestDataTooSmall(NodeRequest request) {
        BigInteger number = new BigInteger(request.getStringDataValue(Params.NUMBER.getValue()));
        return number.bitLength() < 50;
    }

    @Override
    public SubTask newInstance() {
        return new EllipticCurveFactorTask();
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
        return true;
    }

}