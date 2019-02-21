package ru.babobka.nodeslaveserver.server.pipeline.step;

import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodesecurity.auth.AuthCredentials;
import ru.babobka.nodesecurity.auth.AuthResult;
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
public class AuthStepTest {
    private SlaveAuthService slaveAuthService;
    private AuthStep authStep;
    private PipeContext pipeContext;
    private NodeConnection connection;
    private AuthCredentials authCredentials;

    @Before
    public void setUp() {
        connection = mock(NodeConnection.class);
        authCredentials = mock(AuthCredentials.class);
        pipeContext = new PipeContext(connection, authCredentials);
        slaveAuthService = mock(SlaveAuthService.class);
        Container.getInstance().put(container -> {
            container.put(slaveAuthService);
        });
        authStep = new AuthStep();
    }

    @Test
    public void testExecuteFailedAuth() throws IOException {
        when(authCredentials.getLogin()).thenReturn("abc");
        when(authCredentials.getPassword()).thenReturn("xyz");
        AuthResult authResult = AuthResult.fail();
        when(slaveAuthService.authClient(connection, authCredentials.getLogin(), authCredentials.getPassword())).thenReturn(authResult);
        assertFalse(authStep.execute(pipeContext));
    }

    @Test
    public void testExecuteSuccessAuth() throws IOException {
        when(authCredentials.getLogin()).thenReturn("abc");
        when(authCredentials.getPassword()).thenReturn("xyz");
        AuthResult authResult = AuthResult.success("abc", new byte[]{0});
        when(slaveAuthService.authClient(connection, authCredentials.getLogin(), authCredentials.getPassword())).thenReturn(authResult);
        assertTrue(authStep.execute(pipeContext));
    }

    @Test
    public void testExecuteException() throws IOException {
        when(authCredentials.getLogin()).thenReturn("abc");
        when(authCredentials.getPassword()).thenReturn("xyz");
        when(slaveAuthService.authClient(connection, authCredentials.getLogin(), authCredentials.getPassword())).thenThrow(new IOException());
        assertFalse(authStep.execute(pipeContext));
    }
}
