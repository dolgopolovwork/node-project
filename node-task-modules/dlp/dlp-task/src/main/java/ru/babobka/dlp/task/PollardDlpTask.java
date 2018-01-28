package ru.babobka.dlp.task;

import ru.babobka.dlp.model.PollardDlpDataValidators;
import ru.babobka.dlp.model.PollardDlpDistributor;
import ru.babobka.dlp.model.PollardDlpReducer;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodetask.model.*;
import ru.babobka.nodeutils.container.Container;

import java.math.BigInteger;

/**
 * Created by dolgopolov.a on 08.12.15.
 */
public class PollardDlpTask extends SubTask {

    static final String DESCRIPTION = "This task solves DLP in prime field";
    private final PollardDlpDistributor distributor = Container.getInstance().get(PollardDlpDistributor.class);
    private final PollardDlpReducer reducer = Container.getInstance().get(PollardDlpReducer.class);
    private final PollardDlpTaskExecutor taskExecutor = new PollardDlpTaskExecutor();
    private final PollardDlpDataValidators dataValidators = Container.getInstance().get(PollardDlpDataValidators.class);

    PollardDlpTask() {
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
        return dataValidators;
    }

    @Override
    public boolean isRequestDataTooSmall(NodeRequest request) {
        BigInteger mod = request.getDataValue(Params.MOD.getValue());
        return mod.bitLength() < 32;
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