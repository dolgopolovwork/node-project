package ru.babobka.nodetester.slave;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import ru.babobka.nodeslaveserver.server.SlaveServer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;

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

    private SimpleLogger logger;

    @Before
    public void setUp() throws IOException {
        logger = mock(SimpleLogger.class);
        Container.getInstance().put(logger);
        PowerMockito.mockStatic(SlaveServerRunner.class);
        BDDMockito.given(SlaveServerRunner.runSlaveServer(anyString(), anyString())).willReturn(null);
    }

    @Test
    public void onAwakeEmptyList() {
        GlitchThread glitchThread = spy(new GlitchThread("login", "password", new ArrayList<>()));
        glitchThread.onAwake();
        verify(glitchThread, never()).removeRandomSlave(anyList());
    }

    @Test
    public void onAwake() {
        SlaveServer slaveServer = mock(SlaveServer.class);
        List<SlaveServer> slaveServerList = new ArrayList<>(Arrays.asList(slaveServer, slaveServer));
        GlitchThread glitchThread = spy(new GlitchThread("login", "password", slaveServerList));
        glitchThread.onAwake();
        verify(glitchThread).removeRandomSlave(slaveServerList);
    }

}
