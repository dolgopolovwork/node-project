package ru.babobka.nodemasterserver.server;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodebusiness.dao.CacheDAO;
import ru.babobka.nodebusiness.service.NodeUsersService;
import ru.babobka.nodemasterserver.client.IncomingClientListenerThread;
import ru.babobka.nodemasterserver.slave.IncomingSlaveListenerThread;
import ru.babobka.nodemasterserver.slave.SlavesStorage;
import ru.babobka.nodemasterserver.thread.HeartBeatingThread;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.vsjws.webserver.WebServer;

import java.io.IOException;

import static org.mockito.Mockito.*;

/**
 * Created by 123 on 19.09.2017.
 */
public class MasterServerTest {

    private MasterServer masterServer;
    private Thread incomingClientsThread;
    private Thread heartBeatingThread;
    private Thread listenerThread;
    private WebServer webServer;
    private SlavesStorage slavesStorage;
    private MasterServerConfig masterServerConfig;
    private NodeUsersService nodeUsersService;
    private SimpleLogger logger;
    private CacheDAO cacheDAO;

    @Before
    public void setUp() {
        nodeUsersService = mock(NodeUsersService.class);
        masterServerConfig = mock(MasterServerConfig.class);
        heartBeatingThread = mock(HeartBeatingThread.class);
        listenerThread = mock(IncomingSlaveListenerThread.class);
        cacheDAO = mock(CacheDAO.class);
        webServer = mock(WebServer.class);
        incomingClientsThread = mock(IncomingClientListenerThread.class);
        slavesStorage = mock(SlavesStorage.class);
        logger = mock(SimpleLogger.class);
        Container.getInstance().put(nodeUsersService);
        Container.getInstance().put(heartBeatingThread);
        Container.getInstance().put(incomingClientsThread);
        Container.getInstance().put(listenerThread);
        Container.getInstance().put(masterServerConfig);
        Container.getInstance().put(webServer);
        Container.getInstance().put(logger);
        Container.getInstance().put(slavesStorage);
        Container.getInstance().put(cacheDAO);
        masterServer = spy(new MasterServer());
    }

    @After
    public void tearDown() {
        Container.getInstance().clear();
    }

    @Test
    public void testRun() {
        masterServer.run();
        verify(listenerThread).start();
        verify(heartBeatingThread).start();
        verify(webServer).start();
    }

    @Test
    public void testRunException() throws IOException {
        doThrow(new RuntimeException()).when(listenerThread).start();
        masterServer.run();
        verify(logger).error(any(RuntimeException.class));
        verify(masterServer).clear();
    }

    @Test
    public void testInterruptAndJoin() throws InterruptedException {
        Thread thread = mock(Thread.class);
        masterServer.interruptAndJoin(thread);
        verify(thread).interrupt();
    }

    @Test
    public void testClear() throws IOException {
        masterServer.clear();
        verify(masterServer).interruptAndJoin(webServer);
        verify(masterServer).interruptAndJoin(listenerThread);
        verify(masterServer).interruptAndJoin(heartBeatingThread);
        verify(cacheDAO).close();
    }

    @Test
    public void testInterrupt() {
        masterServer.interrupt();
        verify(masterServer).clear();
    }

}
