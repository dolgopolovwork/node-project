package ru.babobka.nodeslaveserver.server.pipeline.step;

import org.apache.log4j.Logger;
import ru.babobka.nodeslaveserver.server.pipeline.PipeContext;
import ru.babobka.nodeutils.func.pipeline.Step;

import java.io.IOException;

/**
 * Created by 123 on 07.06.2018.
 */
public class SessionCreatingStep implements Step<PipeContext> {
    private static final Logger logger = Logger.getLogger(SessionCreatingStep.class);

    @Override
    public boolean execute(PipeContext pipeContext) {
        try {
            boolean sessionWasCreated = pipeContext.getConnection().receive();
            if (!sessionWasCreated) {
                logger.error("cannot create session due to network error");
                return false;
            }
            return true;
        } catch (IOException e) {
            logger.error("cannot create session due to network error", e);
            return false;
        }
    }
}
