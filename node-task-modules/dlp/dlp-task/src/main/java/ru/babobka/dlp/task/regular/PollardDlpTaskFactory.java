package ru.babobka.dlp.task.regular;

import ru.babobka.dlp.DlpServiceApplicationContainer;
import ru.babobka.dlp.model.regular.PollardDlpDataValidators;
import ru.babobka.dlp.model.regular.PollardDlpDistributor;
import ru.babobka.dlp.model.regular.PollardDlpReducer;
import ru.babobka.nodetask.model.TaskFactory;
import ru.babobka.nodeutils.container.AbstractApplicationContainer;
import ru.babobka.nodeutils.container.Container;

/**
 * Created by 123 on 12.10.2017.
 */
public class PollardDlpTaskFactory extends TaskFactory<PollardDlpTask> {

    public PollardDlpTaskFactory() {
        super(PollardDlpTask.class);
    }

    @Override
    public PollardDlpTask createTask() {
        return new PollardDlpTask();
    }

    @Override
    public AbstractApplicationContainer getApplicationContainer() {
        return new PollardDlpTaskApplicationContainer();
    }

    private static class PollardDlpTaskApplicationContainer extends AbstractApplicationContainer {

        @Override
        protected void containImpl(Container container) {
            container.put(new DlpServiceApplicationContainer());
            container.put(new PollardDlpDistributor());
            container.putIfNotExists(new PollardDlpDataValidators());
            container.put(new PollardDlpReducer());
        }
    }
}
