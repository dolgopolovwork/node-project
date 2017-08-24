package ru.babobka.nodemasterserver.thread;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodemasterserver.client.ClientStorage;
import ru.babobka.nodemasterserver.server.MasterServerConfig;
import ru.babobka.nodemasterserver.slave.SlavesStorage;
import ru.babobka.nodeutils.container.ApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;

import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by 123 on 18.09.2017.
 */
public class HeartBeatingThreadTest {

    private HeartBeatingThread heartBeatingThread;
    private MasterServerConfig masterServerConfig;
    private SimpleLogger logger;
    private SlavesStorage slavesStorage;
    private ClientStorage clientStorage;

    @Before
    public void setUp() {
        slavesStorage = mock(SlavesStorage.class);
        masterServerConfig = mock(MasterServerConfig.class);
        logger = mock(SimpleLogger.class);
        clientStorage = mock(ClientStorage.class);
        new ApplicationContainer() {
            @Override
            public void contain(Container container) {
                container.put(slavesStorage);
                container.put(masterServerConfig);
                container.put(logger);
                container.put(clientStorage);
            }
        }.contain(Container.getInstance());
        heartBeatingThread = new HeartBeatingThread();
    }

    @After
    public void tearDown() {
        Container.getInstance().clear();
    }


    @Test
    public void testOnAwake() throws IOException {
        heartBeatingThread.onAwake();
        verify(slavesStorage).heartBeatAllSlaves();
        verify(clientStorage).heartBeatAllClients();
    }

}
