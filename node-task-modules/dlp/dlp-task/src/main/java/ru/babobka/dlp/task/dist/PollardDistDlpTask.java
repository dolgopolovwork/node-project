package ru.babobka.dlp.task.dist;

import ru.babobka.dlp.model.dist.PollardDlpDistDataValidators;
import ru.babobka.dlp.model.dist.PollardDlpDistDistributor;
import ru.babobka.dlp.model.dist.PollardDlpDistReducer;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodetask.model.*;
import ru.babobka.nodeutils.container.Container;

/**
 * Created by 123 on 09.07.2018.
 */
public class PollardDistDlpTask extends SubTask {
    static final String DESCRIPTION = "This task also solves DLP in prime field, but in distributed fashion";
    private final PollardDlpDistDistributor distributor = Container.getInstance().get(PollardDlpDistDistributor.class);
    private final PollardDlpDistReducer reducer = Container.getInstance().get(PollardDlpDistReducer.class);
    private final PollardDlpDistDataValidators validators = Container.getInstance().get(PollardDlpDistDataValidators.class);
    private final PollardDlpDistTaskExecutor taskExecutor = new PollardDlpDistTaskExecutor();

    PollardDistDlpTask() {
    }

    @Override
    public TaskExecutor getTaskExecutor() {
        return taskExecutor;
    }

    @Override
    public DataValidators getDataValidators() {
        return validators;
    }

    @Override
    public boolean isSingleNodeTask(NodeRequest request) {
        return true;
    }

    @Override
    public RequestDistributor getDistributor() {
        return distributor;
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }

    @Override
    public boolean isRaceStyle() {
        return false;
    }

    @Override
    public Reducer getReducer() {
        return reducer;
    }

    @Override
    public boolean enableCache() {
        return false;
    }
}
