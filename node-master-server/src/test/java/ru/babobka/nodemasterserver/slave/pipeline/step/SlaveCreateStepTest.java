package ru.babobka.nodemasterserver.slave.pipeline.step;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodeconfigs.master.MasterServerConfig;
import ru.babobka.nodemasterserver.slave.Sessions;
import ru.babobka.nodemasterserver.slave.pipeline.PipeContext;
import ru.babobka.nodesecurity.auth.AuthResult;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Created by 123 on 08.06.2018.
 */
public class SlaveCreateStepTest {
    private SlaveCreateStep slaveCreateStep;
    private Sessions sessions;

    private MasterServerConfig masterServerConfig;
    private NodeConnection connection;
    private AuthResult authResult;
    private PipeContext pipeContext;

    @Before
    public void testSetUp() {
        authResult = mock(AuthResult.class);
        connection = mock(NodeConnection.class);
        pipeContext = new PipeContext(connection);
        pipeContext.setAuthResult(authResult);
        sessions = mock(Sessions.class);

        masterServerConfig = mock(MasterServerConfig.class);
        Container.getInstance().put(container -> {
            container.put(sessions);

            container.put(masterServerConfig);
        });
        slaveCreateStep = spy(new SlaveCreateStep());
    }

    @After
    public void tearDown() {
        Container.getInstance().clear();
    }

    @Test
    public void testExecuteNotAbleToRunNewSlave() throws IOException {
        doReturn(false).when(slaveCreateStep).isAbleToCreateNewSlave(authResult);
        assertFalse(slaveCreateStep.execute(pipeContext));
        verify(connection).send(false);
    }

    @Test
    public void testExecuteAbleToRunNewSlave() throws IOException {
        doReturn(true).when(slaveCreateStep).isAbleToCreateNewSlave(authResult);
        assertTrue(slaveCreateStep.execute(pipeContext));
        verify(connection, never()).send(any());
    }

    @Test
    public void testExecuteException() throws IOException {
        doReturn(false).when(slaveCreateStep).isAbleToCreateNewSlave(authResult);
        doThrow(new IOException()).when(connection).send(any());
        assertFalse(slaveCreateStep.execute(pipeContext));
    }
}
