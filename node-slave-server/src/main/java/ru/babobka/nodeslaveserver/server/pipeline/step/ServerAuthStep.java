package ru.babobka.nodeslaveserver.server.pipeline.step;

import org.apache.log4j.Logger;
import ru.babobka.nodeslaveserver.server.pipeline.PipeContext;
import ru.babobka.nodeslaveserver.service.SlaveAuthService;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.func.pipeline.Step;

import java.io.IOException;

/**
 * Created by 123 on 07.06.2018.
 */
public class ServerAuthStep implements Step<PipeContext> {
    private static final Logger logger = Logger.getLogger(ServerAuthStep.class);
    private final SlaveAuthService authService = Container.getInstance().get(SlaveAuthService.class);

    @Override
    public boolean execute(PipeContext pipeContext) {
        try {
            boolean serverAuthSuccess = authService.authServer(pipeContext.getConnection());
            if (!serverAuthSuccess) {
                logger.error("server auth fail");
                return false;
            }
            return true;
        } catch (IOException e) {
            logger.error("cannot authenticate server due to network error", e);
            return false;
        }
    }
}
