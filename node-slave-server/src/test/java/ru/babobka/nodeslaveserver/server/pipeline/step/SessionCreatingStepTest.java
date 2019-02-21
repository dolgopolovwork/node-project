package ru.babobka.nodeslaveserver.server.pipeline.step;

import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodesecurity.auth.AuthCredentials;
import ru.babobka.nodeslaveserver.server.pipeline.PipeContext;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by 123 on 09.06.2018.
 */
public class SessionCreatingStepTest {

    private PipeContext pipeContext;
    private NodeConnection connection;
    private SessionCreatingStep sessionCreatingStep;

    @Before
    public void setUp() {
        connection = mock(NodeConnection.class);
        pipeContext = new PipeContext(connection, mock(AuthCredentials.class));
        sessionCreatingStep = new SessionCreatingStep();
    }

    @Test
    public void testExecuteSessionNotCreated() throws IOException {
        when(connection.receive()).thenReturn(false);
        assertFalse(sessionCreatingStep.execute(pipeContext));
    }

    @Test
    public void testExecuteSessionCreated() throws IOException {
        when(connection.receive()).thenReturn(true);
        assertTrue(sessionCreatingStep.execute(pipeContext));
    }

    @Test
    public void testExecuteException() throws IOException {
        when(connection.receive()).thenThrow(new IOException());
        assertFalse(sessionCreatingStep.execute(pipeContext));
    }
}