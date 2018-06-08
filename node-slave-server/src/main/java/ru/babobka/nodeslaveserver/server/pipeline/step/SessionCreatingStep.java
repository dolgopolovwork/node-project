package ru.babobka.nodeslaveserver.server.pipeline.step;

import ru.babobka.nodeslaveserver.server.pipeline.PipeContext;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.func.pipeline.Step;
import ru.babobka.nodeutils.logger.NodeLogger;

import java.io.IOException;

/**
 * Created by 123 on 07.06.2018.
 */
public class SessionCreatingStep implements Step<PipeContext> {
    private final NodeLogger nodeLogger = Container.getInstance().get(NodeLogger.class);

    @Override
    public boolean execute(PipeContext pipeContext) {
        try {
            boolean sessionWasCreated = pipeContext.getConnection().receive();
            if (!sessionWasCreated) {
                nodeLogger.error("cannot create session due to network error");
                return false;
            }
            return true;
        } catch (IOException e) {
            nodeLogger.error("cannot create session due to network error");
            return false;
        }
    }
}
