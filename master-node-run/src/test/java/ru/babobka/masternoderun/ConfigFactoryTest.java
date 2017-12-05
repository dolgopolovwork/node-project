package ru.babobka.masternoderun;

import com.google.gson.Gson;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodemasterserver.server.MasterServerConfig;
import ru.babobka.nodeutils.container.ApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.util.StreamUtil;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by 123 on 06.12.2017.
 */
public class ConfigFactoryTest {

    private StreamUtil streamUtil;
    private ConfigFactory configFactory;

    @Before
    public void setUp() {
        streamUtil = mock(StreamUtil.class);
        new ApplicationContainer() {
            @Override
            public void contain(Container container) {
                container.put(new Gson());
                container.put(streamUtil);
            }
        }.contain(Container.getInstance());
        configFactory = new ConfigFactory();
    }

    @After
    public void tearDown() {
        Container.getInstance().clear();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNullPath() throws IOException {
        configFactory.create(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateEmptyPath() throws IOException {
        configFactory.create("");
    }

    @Test(expected = IOException.class)
    public void testCreateInvalidJson() throws IOException {
        String path = "C://path";
        when(streamUtil.readFile(path)).thenReturn("not json at all");
        configFactory.create(path);
    }

    @Test(expected = IOException.class)
    public void testCreateIOExceptionWhileRead() throws IOException {
        String path = "C://path";
        when(streamUtil.readFile(path)).thenThrow(new IOException());
        configFactory.create(path);
    }

    @Test
    public void testCreate() throws IOException {
        String path = "C://path";
        String json = "{\n" +
                "   \"debugMode\":true,\n" +
                "   \"authTimeOutMillis\":2000,\n" +
                "   \"slaveListenerPort\":9090,\n" +
                "   \"clientListenerPort\":9999,\n" +
                "   \"requestTimeOutMillis\":5000,\n" +
                "   \"heartBeatTimeOutMillis\":2000,\n" +
                "   \"webListenerPort\":8080\n" + "}";
        when(streamUtil.readFile(path)).thenReturn(json);
        MasterServerConfig config = configFactory.create(path);
        assertEquals(config.isDebugMode(), true);
        assertEquals(config.getAuthTimeOutMillis(), 2000L);
        assertEquals(config.getWebListenerPort(), 8080);
        assertEquals(config.getSlaveListenerPort(), 9090);
    }
}

