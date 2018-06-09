package ru.babobka.nodeslaveserver.server.pipeline.step;

import ru.babobka.nodeslaveserver.server.pipeline.PipeContext;
import ru.babobka.nodeslaveserver.service.SlaveAuthService;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.func.pipeline.Step;
import ru.babobka.nodeutils.logger.NodeLogger;

import java.io.IOException;

/**
 * Created by 123 on 07.06.2018.
 */
public class ServerAuthStep implements Step<PipeContext> {
    private final NodeLogger nodeLogger = Container.getInstance().get(NodeLogger.class);
    private final SlaveAuthService authService = Container.getInstance().get(SlaveAuthService.class);

    @Override
    public boolean execute(PipeContext pipeContext) {
        try {
            boolean serverAuthSuccess = authService.authServer(pipeContext.getConnection());
            if (!serverAuthSuccess) {
                nodeLogger.error("server auth fail");
                return false;
            }
            return true;
        } catch (IOException e) {
            nodeLogger.error("cannot authenticate server due to network error");
            return false;
        }
    }
}
