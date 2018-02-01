package ru.babobka.nodetester.benchmark;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.babobka.nodemasterserver.server.MasterServer;
import ru.babobka.nodetester.master.MasterServerRunner;
import ru.babobka.nodetester.slave.cluster.SlaveServerCluster;
import ru.babobka.nodetester.slave.SlaveServerRunner;
import ru.babobka.nodeutils.container.Container;

import java.io.IOException;

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
    private NodeBenchmark nodeBenchmark;
    private MasterServer masterServer;

    @Before
    public void setUp() {
        masterServer = mock(MasterServer.class);
        nodeBenchmark = spy(NodeBenchmark.class);
        PowerMockito.mockStatic(MasterServerRunner.class);
        PowerMockito.mockStatic(SlaveServerRunner.class);
        BDDMockito.given(MasterServerRunner.runMasterServer()).willReturn(masterServer);
        BDDMockito.given(MasterServerRunner.init()).willReturn(null);
        BDDMockito.given(SlaveServerRunner.init()).willReturn(null);
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
    public void testRunException() throws IOException {
        int slaveThreads = 2;
        int slaves = 1;
        doThrow(new IOException()).when(nodeBenchmark).createCluster(anyString(), anyString(), anyInt());
        nodeBenchmark.run(slaves, slaveThreads);
        verify(nodeBenchmark, never()).onBenchmark();
        assertEquals((int) Container.getInstance().get("service-threads"), slaveThreads);
        verify(masterServer).interrupt();
    }

    @Test
    public void testRun() throws IOException, InterruptedException {
        int slaveThreads = 2;
        int slaves = 1;
        SlaveServerCluster cluster = mock(SlaveServerCluster.class);
        doReturn(cluster).when(nodeBenchmark).createCluster(anyString(), anyString(), anyInt());
        nodeBenchmark.run(slaves, slaveThreads);
        verify(nodeBenchmark).onBenchmark();
        verify(cluster).start();
        assertEquals((int) Container.getInstance().get("service-threads"), slaveThreads);
        verify(masterServer).interrupt();
    }
}
