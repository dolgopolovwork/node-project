package ru.babobka.nodebusiness.service;

import org.junit.BeforeClass;
import org.junit.Test;
import ru.babobka.nodeserials.NodeAuthRequest;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Created by 123 on 13.08.2017.
 */
public class MasterAuthServiceTest {

    private static final SimpleLogger simpleLogger = mock(SimpleLogger.class);

    private static final NodeUsersService nodeUsersService = mock(NodeUsersService.class);

    private static AuthService authService;

    @BeforeClass
    public static void setUp() {
        Container.getInstance().put(simpleLogger);
        Container.getInstance().put(nodeUsersService);
        authService = new MasterAuthService();
    }

    @Test
    public void testAuth() throws IOException {
        String login = "abc";
        String password = "xyz";
        NodeConnection nodeConnection = mock(NodeConnection.class);
        NodeAuthRequest nodeAuthRequest = generateAuthRequest(login, password);
        when(nodeConnection.receive()).thenReturn(nodeAuthRequest);
        when(nodeUsersService.auth(login, nodeAuthRequest.getHashedPassword())).thenReturn(true);
        assertTrue(authService.auth(nodeConnection));
        verify(nodeConnection).send(true);
    }

    @Test
    public void testAuthBadPassword() throws IOException {
        String login = "abc";
        String password = "xyz";
        NodeConnection nodeConnection = mock(NodeConnection.class);
        NodeAuthRequest nodeAuthRequest = generateAuthRequest(login, password);
        when(nodeConnection.receive()).thenReturn(nodeAuthRequest);
        when(nodeUsersService.auth(login, nodeAuthRequest.getHashedPassword())).thenReturn(false);
        assertFalse(authService.auth(nodeConnection));
        verify(nodeConnection).send(false);
    }

    @Test
    public void testAuthException() throws IOException {
        NodeConnection nodeConnection = mock(NodeConnection.class);
        when(nodeConnection.receive()).thenThrow(new IOException());
        assertFalse(authService.auth(nodeConnection));
        verify(simpleLogger).error(any(IOException.class));
    }

    private NodeAuthRequest generateAuthRequest(String login, String password) {
        return new NodeAuthRequest(login, password);
    }
}
