package ru.babobka.nodeslaveserver.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodeserials.NodeAuthRequest;
import ru.babobka.nodeslaveserver.server.SlaveServerConfig;
import ru.babobka.nodeutils.container.ApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created by 123 on 02.09.2017.
 */
public class SlaveAuthServiceTest {
    private SlaveServerConfig slaveServerConfig;
    private AuthService authService;

    @Before
    public void setUp() {
        slaveServerConfig = mock(SlaveServerConfig.class);
        new ApplicationContainer() {
            @Override
            public void contain(Container container) {
                container.put(slaveServerConfig);
            }
        }.contain(Container.getInstance());
        authService = new SlaveAuthService();
    }

    @After
    public void tearDown() {
        Container.getInstance().clear();
    }

    @Test
    public void testAuth() throws IOException {
        when(slaveServerConfig.getAuthTimeoutMillis()).thenReturn(10);
        NodeConnection connection = mock(NodeConnection.class);
        boolean authResult = true;
        when(connection.receive()).thenReturn(authResult);
        assertEquals(authService.auth(connection, "abc", "123"), authResult);
        verify(connection).send(any(NodeAuthRequest.class));
    }

    @Test(expected = IOException.class)
    public void testAuthException() throws IOException {
        when(slaveServerConfig.getAuthTimeoutMillis()).thenReturn(10);
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.receive()).thenThrow(new IOException());
        authService.auth(connection, "abc", "123");
    }

}
