package ru.babobka.nodemasterserver.slave.pipeline.step;

import org.apache.log4j.Logger;
import ru.babobka.nodemasterserver.service.MasterAuthService;
import ru.babobka.nodemasterserver.slave.pipeline.PipeContext;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.func.pipeline.Step;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;

/**
 * Created by 123 on 07.06.2018.
 */
public class ServerAuthStep implements Step<PipeContext> {
    private static final Logger logger = Logger.getLogger(ServerAuthStep.class);
    private final MasterAuthService authService = Container.getInstance().get(MasterAuthService.class);

    @Override
    public boolean execute(PipeContext pipeContext) {
        NodeConnection connection = pipeContext.getConnection();
        try {
            if (!authService.authServer(connection)) {
                logger.error("server authentication fail");
                return false;
            }
            return true;
        } catch (IOException e) {
            logger.error("cannot authenticate master-server due to network error");
            return false;
        }
    }
}
