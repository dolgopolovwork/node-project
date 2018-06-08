package ru.babobka.nodemasterserver.slave.pipeline.step;

import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodemasterserver.service.MasterAuthService;
import ru.babobka.nodemasterserver.slave.pipeline.PipeContext;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.NodeLogger;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by 123 on 08.06.2018.
 */
public class ServerAuthStepTest {

    private MasterAuthService masterAuthService;
    private NodeLogger nodeLogger;
    private ServerAuthStep serverAuthStep;
    private NodeConnection connection;
    private PipeContext pipeContext;

    @Before
    public void setUp() {
        masterAuthService = mock(MasterAuthService.class);
        nodeLogger = mock(NodeLogger.class);
        connection = mock(NodeConnection.class);
        pipeContext = new PipeContext(connection);
        Container.getInstance().put(container -> {
            container.put(masterAuthService);
            container.put(nodeLogger);
        });
        serverAuthStep = new ServerAuthStep();
    }

    @Test
    public void testExecuteAuthFail() throws IOException {
        when(masterAuthService.authServer(connection)).thenReturn(false);
        assertFalse(serverAuthStep.execute(pipeContext));
    }

    @Test
    public void testExecuteAuthSuccess() throws IOException {
        when(masterAuthService.authServer(connection)).thenReturn(true);
        assertTrue(serverAuthStep.execute(pipeContext));
    }

    @Test
    public void testExecuteAuthException() throws IOException {
        when(masterAuthService.authServer(connection)).thenThrow(new IOException());
        assertFalse(serverAuthStep.execute(pipeContext));
    }
}

