package ru.babobka.nodemasterserver.slave.pipeline.step;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodeconfigs.master.MasterServerConfig;
import ru.babobka.nodeconfigs.master.TimeConfig;
import ru.babobka.nodemasterserver.service.MasterAuthService;
import ru.babobka.nodemasterserver.slave.pipeline.PipeContext;
import ru.babobka.nodesecurity.auth.AuthResult;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.NodeLogger;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by 123 on 08.06.2018.
 */
public class SlaveAuthStepTest {

    private SlaveAuthStep slaveAuthStep;
    private MasterAuthService masterAuthService;
    private MasterServerConfig masterServerConfig;
    private NodeLogger nodeLogger;
    private NodeConnection connection;
    private PipeContext pipeContext;

    @Before
    public void setUp() {
        masterAuthService = mock(MasterAuthService.class);
        masterServerConfig = mock(MasterServerConfig.class);
        nodeLogger = mock(NodeLogger.class);
        connection = mock(NodeConnection.class);
        pipeContext = new PipeContext(connection);
        Container.getInstance().put(container -> {
            container.put(masterAuthService);
            container.put(masterServerConfig);
            container.put(nodeLogger);
        });
        slaveAuthStep = new SlaveAuthStep();
    }

    @After
    public void tearDown() {
        Container.getInstance().clear();
    }

    @Test
    public void testExecuteAuthFail() throws IOException {
        TimeConfig timeConfig = new TimeConfig();
        when(masterServerConfig.getTime()).thenReturn(timeConfig);
        AuthResult authResult = AuthResult.fail();
        when(masterAuthService.authClient(connection)).thenReturn(authResult);
        assertFalse(slaveAuthStep.execute(pipeContext));
    }

    @Test
    public void testExecuteAuthSuccess() throws IOException {
        TimeConfig timeConfig = new TimeConfig();
        when(masterServerConfig.getTime()).thenReturn(timeConfig);
        AuthResult authResult = AuthResult.success("abc", new byte[]{0});
        when(masterAuthService.authClient(connection)).thenReturn(authResult);
        assertTrue(slaveAuthStep.execute(pipeContext));
        assertEquals(pipeContext.getAuthResult(), authResult);
    }

    @Test
    public void testExecuteAuthException() throws IOException {
        TimeConfig timeConfig = new TimeConfig();
        when(masterServerConfig.getTime()).thenReturn(timeConfig);
        when(masterAuthService.authClient(connection)).thenThrow(new IOException());
        assertFalse(slaveAuthStep.execute(pipeContext));
    }
}
