package ru.babobka.nodetester.benchmark;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.babobka.nodeclient.Client;
import ru.babobka.nodemasterserver.server.MasterServer;
import ru.babobka.nodemasterserver.server.config.MasterServerConfig;
import ru.babobka.nodemasterserver.server.config.PortConfig;
import ru.babobka.nodemasterserver.server.config.SecurityConfig;
import ru.babobka.nodesecurity.rsa.RSAConfigFactory;
import ru.babobka.nodesecurity.rsa.RSAPublicKey;
import ru.babobka.nodetester.master.MasterServerRunner;
import ru.babobka.nodetester.slave.SlaveServerRunner;
import ru.babobka.nodetester.slave.cluster.SlaveServerCluster;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.container.Properties;
import ru.babobka.nodeutils.key.UtilKey;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Created by 123 on 31.01.2018.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({MasterServerRunner.class, SlaveServerRunner.class})
public class NodeBenchmarkTest {
    private Client client;
    private NodeBenchmark nodeBenchmark;
    private MasterServer masterServer;
    private MasterServerConfig config;
    private int port = 1010;
    private int tests = 100;

    @Before
    public void setUp() {
        config = new MasterServerConfig();
        PortConfig portConfig = new PortConfig();
        portConfig.setClientListenerPort(port);
        config.setPorts(portConfig);
        SecurityConfig securityConfig = new SecurityConfig();
        securityConfig.setRsaConfig(RSAConfigFactory.create(128));
        config.setSecurity(securityConfig);
        Container.getInstance().put(config);
        client = mock(Client.class);
        masterServer = mock(MasterServer.class);
        nodeBenchmark = spy(new NodeBenchmark("testApp", tests) {
            @Override
            protected String getDescription() {
                return null;
            }

            @Override
            protected void onBenchmark(Client client, AtomicLong timerStorage) throws IOException, ExecutionException, InterruptedException {

            }
        });
        doNothing().when(nodeBenchmark).startMonitoring();
        PowerMockito.mockStatic(MasterServerRunner.class);
        PowerMockito.mockStatic(SlaveServerRunner.class);
        BDDMockito.given(MasterServerRunner.runMasterServer()).willReturn(masterServer);
        BDDMockito.given(MasterServerRunner.init()).willReturn(null);
        BDDMockito.given(SlaveServerRunner.init(any(RSAPublicKey.class))).willReturn(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRunBadSlaves() throws IOException {
        nodeBenchmark.run(-1, 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRunBadThreads() throws IOException {
        nodeBenchmark.run(1, -2);
    }

    @Test
    public void testRunException() throws IOException, ExecutionException, InterruptedException {
        int slaveThreads = 2;
        int slaves = 1;
        doThrow(new IOException()).when(nodeBenchmark).createCluster(anyString(), anyString(), anyInt());
        nodeBenchmark.run(slaves, slaveThreads);
        verify(nodeBenchmark, never()).onBenchmark(eq(client), any(AtomicLong.class));
        assertEquals((int) Container.getInstance().get(UtilKey.SERVICE_THREADS_NUM), slaveThreads);
        verify(masterServer).interrupt();
    }

    @Test
    public void testRun() throws IOException, InterruptedException, ExecutionException {
        int slaveThreads = 2;
        int slaves = 1;
        SlaveServerCluster cluster = mock(SlaveServerCluster.class);
        doReturn(cluster).when(nodeBenchmark).createCluster(anyString(), anyString(), anyInt());
        nodeBenchmark.run(slaves, slaveThreads);
        doReturn(null).when(nodeBenchmark).executeCycledBenchmark(tests);
        verify(nodeBenchmark).executeCycledBenchmark(tests);
        verify(cluster).start();
        assertEquals(Properties.getInt(UtilKey.SERVICE_THREADS_NUM), slaveThreads);
        verify(masterServer).interrupt();
        verify(nodeBenchmark).startMonitoring();
    }
}
