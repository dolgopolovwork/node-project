package ru.babobka.nodemasterserver.slave;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodeconfigs.master.MasterServerConfig;
import ru.babobka.nodeconfigs.master.TimeConfig;
import ru.babobka.nodemasterserver.slave.pipeline.PipeContext;
import ru.babobka.nodemasterserver.slave.pipeline.SlaveCreatingPipelineFactory;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.func.pipeline.Pipeline;
import ru.babobka.nodeutils.log.LoggerInit;
import ru.babobka.nodeutils.network.NodeConnection;
import ru.babobka.nodeutils.network.NodeConnectionFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.mockito.Mockito.*;

/**
 * Created by 123 on 19.09.2017.
 */
public class IncomingSlaveListenerThreadTest {

    static {
        LoggerInit.initConsoleLogger();
    }

    private IncomingSlaveListenerThread incomingSlaveListenerThread;
    private NodeConnectionFactory nodeConnectionFactory;

    private ServerSocket serverSocket;
    private MasterServerConfig masterServerConfig;
    private SlaveCreatingPipelineFactory pipelineFactory;

    @Before
    public void setUp() {
        masterServerConfig = mock(MasterServerConfig.class);
        nodeConnectionFactory = mock(NodeConnectionFactory.class);
        serverSocket = mock(ServerSocket.class);
        pipelineFactory = mock(SlaveCreatingPipelineFactory.class);
        Container.getInstance().put(container -> {
            container.put(nodeConnectionFactory);
            container.put(masterServerConfig);
            container.put(pipelineFactory);

        });
        incomingSlaveListenerThread = spy(new IncomingSlaveListenerThread(serverSocket));
    }

    @After
    public void tearDown() {
        Container.getInstance().clear();
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorNullServerSocket() {
        new IncomingSlaveListenerThread(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorClosedServerSocket() {
        when(serverSocket.isClosed()).thenReturn(true);
        new IncomingSlaveListenerThread(serverSocket);
    }

    @Test
    public void testOnExit() throws IOException {
        incomingSlaveListenerThread.onExit();
        verify(serverSocket).close();
    }

    @Test
    public void testOnExitException() throws IOException {
        doThrow(new IOException()).when(serverSocket).close();
        incomingSlaveListenerThread.onExit();
    }

    @Test
    public void testInterrupt() throws IOException {
        incomingSlaveListenerThread.interrupt();
        verify(serverSocket).close();
    }

    @Test
    public void testOnCycleFailedPipeline() throws IOException {
        TimeConfig timeConfig = new TimeConfig();
        timeConfig.setRequestReadTimeOutMillis(100);
        when(masterServerConfig.getTime()).thenReturn(timeConfig);
        Socket socket = mock(Socket.class);
        when(serverSocket.accept()).thenReturn(socket);
        NodeConnection connection = mock(NodeConnection.class);
        when(nodeConnectionFactory.create(socket)).thenReturn(connection);
        Pipeline<PipeContext> pipeline = mock(Pipeline.class);
        when(pipeline.execute(any(PipeContext.class))).thenReturn(false);
        when(pipelineFactory.create(any(PipeContext.class))).thenReturn(pipeline);
        incomingSlaveListenerThread.onCycle();
        verify(pipeline).execute(any(PipeContext.class));
        verify(connection, never()).setReadTimeOut(timeConfig.getRequestReadTimeOutMillis());
    }

    @Test
    public void testOnCycleSuccessPipeline() throws IOException {
        TimeConfig timeConfig = new TimeConfig();
        timeConfig.setRequestReadTimeOutMillis(100);
        when(masterServerConfig.getTime()).thenReturn(timeConfig);
        Socket socket = mock(Socket.class);
        when(serverSocket.accept()).thenReturn(socket);
        NodeConnection connection = mock(NodeConnection.class);
        when(nodeConnectionFactory.create(socket)).thenReturn(connection);
        Pipeline<PipeContext> pipeline = mock(Pipeline.class);
        when(pipeline.execute(any(PipeContext.class))).thenReturn(true);
        when(pipelineFactory.create(any(PipeContext.class))).thenReturn(pipeline);
        incomingSlaveListenerThread.onCycle();
        verify(pipeline).execute(any(PipeContext.class));
        verify(connection).setReadTimeOut(timeConfig.getRequestReadTimeOutMillis());
    }

    @Test
    public void testOnCycleException() throws IOException {
        TimeConfig timeConfig = new TimeConfig();
        timeConfig.setRequestReadTimeOutMillis(100);
        when(masterServerConfig.getTime()).thenReturn(timeConfig);
        Socket socket = mock(Socket.class);
        when(serverSocket.accept()).thenReturn(socket);
        NodeConnection connection = mock(NodeConnection.class);
        when(nodeConnectionFactory.create(socket)).thenReturn(connection);
        Pipeline<PipeContext> pipeline = mock(Pipeline.class);
        when(pipeline.execute(any(PipeContext.class))).thenReturn(true);
        when(pipelineFactory.create(any(PipeContext.class))).thenReturn(pipeline);
        doThrow(new IOException()).when(connection).setReadTimeOut(timeConfig.getRequestReadTimeOutMillis());
        incomingSlaveListenerThread.onCycle();
        verify(connection).close();
    }

}
