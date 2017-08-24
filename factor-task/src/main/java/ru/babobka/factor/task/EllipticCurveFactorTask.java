package ru.babobka.factor.task;

import ru.babobka.factor.model.EllipticFactorDataValidators;
import ru.babobka.factor.model.EllipticFactorDistributor;
import ru.babobka.factor.model.EllipticFactorReducer;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodetask.model.*;
import ru.babobka.nodeutils.container.Container;

import java.math.BigInteger;

/**
 * Created by dolgopolov.a on 08.12.15.
 */
public class EllipticCurveFactorTask extends SubTask {

    static final String DESCRIPTION = "Factorizes a given big composite number using Lenstra algorithm";
    private final EllipticFactorDistributor distributor = Container.getInstance().get(EllipticFactorDistributor.class);
    private final EllipticFactorReducer reducer = Container.getInstance().get(EllipticFactorReducer.class);
    private final EllipticCurveFactorTaskExecutor taskExecutor = new EllipticCurveFactorTaskExecutor();
    private final EllipticFactorDataValidators ellipticFactorDataValidators = Container.getInstance().get(EllipticFactorDataValidators.class);

    EllipticCurveFactorTask() {
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
        return taskExecutor;
    }

    @Override
    public DataValidators getDataValidators() {
        return ellipticFactorDataValidators;
    }

    @Override
    public boolean isRequestDataTooSmall(NodeRequest request) {
        BigInteger number = new BigInteger(request.getStringDataValue(Params.NUMBER.getValue()));
        return number.bitLength() < 50;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }


    @Override
    public boolean isRaceStyle() {
        return true;
    }

}