package ru.babobka.nodeslaveserver.server;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.babobka.nodesecurity.network.ClientSecureNodeConnection;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeslaveserver.controller.ControllerFactory;
import ru.babobka.nodeslaveserver.exception.SlaveStartupException;
import ru.babobka.nodeslaveserver.server.pipeline.PipeContext;
import ru.babobka.nodeslaveserver.server.pipeline.SlavePipelineFactory;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.func.pipeline.Pipeline;
import ru.babobka.nodeutils.network.NodeConnection;
import ru.babobka.nodeutils.network.NodeConnectionFactory;

import java.io.IOException;
import java.net.Socket;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created by 123 on 05.09.2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(SlaveServer.class)
public class SlaveServerTest {

    private NodeConnectionFactory nodeConnectionFactory;
    private SlavePipelineFactory slavePipelineFactory;
    private ClientSecureNodeConnection clientSecureNodeConnection;

    @Before
    public void setUp() {
        clientSecureNodeConnection = mock(ClientSecureNodeConnection.class);
        slavePipelineFactory = mock(SlavePipelineFactory.class);
        nodeConnectionFactory = mock(NodeConnectionFactory.class);
        Container.getInstance().put(container -> {
            container.put(nodeConnectionFactory);
            container.put(slavePipelineFactory);
        });
    }

    @After
    public void tearDown() {
        Container.getInstance().clear();
    }

    @Test(expected = SlaveStartupException.class)
    public void testFailedPipeline() throws IOException {
        Pipeline<PipeContext> slaveCreationPipeline = mock(Pipeline.class);
        when(slaveCreationPipeline.execute(any(PipeContext.class))).thenReturn(false);
        when(slavePipelineFactory.create(any(PipeContext.class))).thenReturn(slaveCreationPipeline);
        Socket socket = mock(Socket.class);
        NodeConnection connection = mock(NodeConnection.class);
        when(nodeConnectionFactory.create(socket)).thenReturn(connection);
        new SlaveServer(socket, "abc", "xyz", mock(ControllerFactory.class));
    }

    @Test
    public void testSuccessPipeline() throws IOException {
        SlaveServer slaveServer = prepareValidSlaveServer();
        assertEquals(slaveServer.getConnection(), clientSecureNodeConnection);
    }

    @Test
    public void testClear() throws IOException {
        SlaveServer slaveServer = prepareValidSlaveServer();
        slaveServer.clear();
        verify(slaveServer.getConnection()).close();
    }

    @Test
    public void testInterrupt() throws IOException {
        SlaveServer slaveServer = prepareValidSlaveServer();
        slaveServer.interrupt();
        verify(slaveServer.getConnection()).send(any(NodeResponse.class));
        verify(slaveServer).clear();
    }

    private SlaveServer prepareValidSlaveServer() throws IOException {
        Pipeline<PipeContext> slaveCreationPipeline = mock(Pipeline.class);
        when(slaveCreationPipeline.execute(any(PipeContext.class))).thenReturn(true);
        when(slavePipelineFactory.create(any(PipeContext.class))).thenReturn(slaveCreationPipeline);
        Socket socket = mock(Socket.class);
        NodeConnection connection = mock(NodeConnection.class);
        when(nodeConnectionFactory.create(socket)).thenReturn(connection);
        PowerMockito.mockStatic(SlaveServer.class);
        BDDMockito.given(SlaveServer.createClientConnection(eq(connection), any(PipeContext.class))).willReturn(clientSecureNodeConnection);
        return spy(new SlaveServer(socket, "abc", "xyz", mock(ControllerFactory.class)));
    }

}
