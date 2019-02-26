package ru.babobka.nodeslaveserver.server.pipeline.step;

import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodesecurity.auth.AuthCredentials;
import ru.babobka.nodeslaveserver.server.pipeline.PipeContext;
import ru.babobka.nodeslaveserver.service.SlaveAuthService;
import ru.babobka.nodeutils.container.Container;

import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by 123 on 09.06.2018.
 */
public class ServerAuthStepTest {
    private NodeConnection connection;
    private PipeContext pipeContext;
    private ServerAuthStep serverAuthStep;

    private SlaveAuthService slaveAuthService;

    @Before
    public void setUp() {
        connection = mock(NodeConnection.class);
        pipeContext = new PipeContext(connection, mock(AuthCredentials.class));

        slaveAuthService = mock(SlaveAuthService.class);
        Container.getInstance().put(container -> {

            container.put(slaveAuthService);
        });
        serverAuthStep = new ServerAuthStep();
    }

    @Test
    public void testExecuteFailedAuth() throws IOException {
        when(slaveAuthService.authServer(connection)).thenReturn(false);
        assertFalse(serverAuthStep.execute(pipeContext));
    }

    @Test
    public void testExecuteSuccessAuth() throws IOException {
        when(slaveAuthService.authServer(connection)).thenReturn(true);
        assertTrue(serverAuthStep.execute(pipeContext));
    }

    @Test
    public void testExecuteException() throws IOException {
        when(slaveAuthService.authServer(connection)).thenThrow(new IOException());
        assertFalse(serverAuthStep.execute(pipeContext));
    }
}
