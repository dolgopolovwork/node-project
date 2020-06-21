package ru.babobka.nodetester.slave;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.babobka.nodeslaveserver.server.SlaveServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Created by 123 on 01.02.2018.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(SlaveServerRunner.class)
public class GlitchThreadTest {

    @Before
    public void setUp() throws IOException {
        PowerMockito.mockStatic(SlaveServerRunner.class);
        BDDMockito.given(SlaveServerRunner.runSlaveServer(anyString())).willReturn(null);
    }

    @Test
    public void testOnAwakeEmptyList() {
        GlitchThread glitchThread = spy(new GlitchThread("login", new ArrayList<>()));
        glitchThread.onCycle();
        verify(glitchThread, never()).removeRandomSlave(anyList());
    }

    @Test
    public void testOnAwake() {
        SlaveServer slaveServer = mock(SlaveServer.class);
        List<SlaveServer> slaveServerList = new ArrayList<>(Arrays.asList(slaveServer, slaveServer));
        GlitchThread glitchThread = spy(new GlitchThread("login", slaveServerList));
        glitchThread.onCycle();
        verify(glitchThread).removeRandomSlave(slaveServerList);
    }

}
