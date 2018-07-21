package ru.babobka.dlp.task.dist;

import ru.babobka.dlp.mapper.PollardDistResultMapper;
import ru.babobka.dlp.model.dist.PollardDlpDistDataValidators;
import ru.babobka.dlp.model.dist.PollardDlpDistDistributor;
import ru.babobka.dlp.model.dist.PollardDlpDistReducer;
import ru.babobka.dlp.model.regular.PollardDlpDataValidators;
import ru.babobka.nodetask.model.TaskFactory;
import ru.babobka.nodeutils.container.AbstractApplicationContainer;
import ru.babobka.nodeutils.container.Container;

/**
 * Created by 123 on 11.07.2018.
 */
public class PollardDlpDistTaskFactory extends TaskFactory<PollardDistDlpTask> {

    public PollardDlpDistTaskFactory() {
        super(PollardDistDlpTask.class);
    }

    @Override
    public PollardDistDlpTask createTask() {
        return new PollardDistDlpTask();
    }

    @Override
    public AbstractApplicationContainer getApplicationContainer() {
        return new PollardDlpDistTaskFactory.PollardDlpDistTaskApplicationContainer();
    }

    private static class PollardDlpDistTaskApplicationContainer extends AbstractApplicationContainer {

        @Override
        protected void containImpl(Container container) {
            container.putIfNotExists(new PollardDlpDataValidators());
            container.put(new PollardDlpDistDataValidators());
            container.put(new PollardDlpDistDistributor());
            container.put(new PollardDlpDistReducer());
            container.put(new PollardDistResultMapper());
        }
    }
}
