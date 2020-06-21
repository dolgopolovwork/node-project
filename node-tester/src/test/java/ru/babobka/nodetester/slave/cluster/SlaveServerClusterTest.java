package ru.babobka.nodetester.slave.cluster;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.babobka.nodeslaveserver.server.SlaveServer;
import ru.babobka.nodetester.slave.GlitchThread;
import ru.babobka.nodetester.slave.SlaveServerRunner;

import java.io.IOException;

import static org.mockito.Mockito.*;

/**
 * Created by 123 on 01.02.2018.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SlaveServerRunner.class, SlaveServerCluster.class})
public class SlaveServerClusterTest {

    private SlaveServer slaveServer;
    private GlitchThread glitchThread;

    @Before
    public void setUp() throws IOException {
        slaveServer = mock(SlaveServer.class);
        glitchThread = mock(GlitchThread.class);
        PowerMockito.mockStatic(SlaveServerRunner.class);
        PowerMockito.mockStatic(SlaveServerCluster.class);
        BDDMockito.given(SlaveServerRunner.getSlaveServer(anyString())).willReturn(slaveServer);
        BDDMockito.given(SlaveServerCluster.createGlitchThread(anyString(), anyList())).willReturn(glitchThread);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullArgs() throws IOException {
        new SlaveServerCluster(null, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeSlaves() throws IOException {
        new SlaveServerCluster("abc", -1);
    }

    @Test
    public void testStart() throws IOException {
        int slaves = 3;
        SlaveServerCluster cluster = new SlaveServerCluster("abc", slaves);
        cluster.start();
        verify(slaveServer, times(slaves)).start();
    }

    @Test
    public void testStartTwice() throws IOException {
        int slaves = 3;
        SlaveServerCluster cluster = new SlaveServerCluster("abc", slaves);
        cluster.start();
        cluster.start();
        verify(slaveServer, times(slaves)).start();
    }

    @Test
    public void testStartGlitchy() throws IOException {
        int slaves = 3;
        SlaveServerCluster cluster = new SlaveServerCluster("abc", slaves, true);
        cluster.start();
        verify(glitchThread).start();
    }

    @Test
    public void testStartNotGlitchy() throws IOException {
        int slaves = 3;
        SlaveServerCluster cluster = new SlaveServerCluster("abc", slaves);
        cluster.start();
        verify(glitchThread, never()).start();
    }

    @Test
    public void testClose() throws IOException {
        int slaves = 3;
        SlaveServerCluster cluster = new SlaveServerCluster("abc", slaves);
        cluster.close();
        verify(slaveServer, times(slaves)).interrupt();
    }

    @Test
    public void testCloseTwice() throws IOException {
        int slaves = 3;
        SlaveServerCluster cluster = new SlaveServerCluster("abc", slaves);
        cluster.close();
        cluster.close();
        verify(slaveServer, times(slaves)).interrupt();
    }

    @Test
    public void testCloseGlitchy() throws IOException {
        int slaves = 3;
        SlaveServerCluster cluster = new SlaveServerCluster("abc", slaves, true);
        cluster.close();
        cluster.close();
        verify(glitchThread).interrupt();
    }
}
