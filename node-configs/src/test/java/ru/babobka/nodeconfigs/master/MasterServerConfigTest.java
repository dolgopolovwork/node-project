package ru.babobka.nodeconfigs.master;

import org.junit.Test;
import ru.babobka.nodesecurity.keypair.Base64KeyPair;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

public class MasterServerConfigTest {

    @Test
    public void testCopyDefaultValues() {
        MasterServerConfig masterServerConfig = new MasterServerConfig();
        MasterServerConfig masterServerConfigCopy = masterServerConfig.copy();
        assertEquals(masterServerConfig, masterServerConfigCopy);
        assertNotSame(masterServerConfig, masterServerConfigCopy);
    }

    @Test
    public void testCopyDefaultSubValues() {
        MasterServerConfig masterServerConfig = new MasterServerConfig();
        masterServerConfig.setFolders(new FolderConfig());
        masterServerConfig.setTime(new TimeConfig());
        masterServerConfig.setModes(new ModeConfig());
        masterServerConfig.setPorts(new PortConfig());
        masterServerConfig.setKeyPair(new Base64KeyPair());
        MasterServerConfig masterServerConfigCopy = masterServerConfig.copy();
        assertEquals(masterServerConfig, masterServerConfigCopy);
        assertNotSame(masterServerConfig, masterServerConfigCopy);
    }


    @Test
    public void testCopyDefaultRandomValues() {
        MasterServerConfig masterServerConfig = new MasterServerConfig();
        FolderConfig folderConfig = new FolderConfig();
        folderConfig.setTasksFolder("abc");
        masterServerConfig.setFolders(folderConfig);
        TimeConfig timeConfig = new TimeConfig();
        timeConfig.setAuthTimeOutMillis(123);
        masterServerConfig.setTime(timeConfig);
        ModeConfig modeConfig = new ModeConfig();
        modeConfig.setTestUserMode(true);
        masterServerConfig.setModes(modeConfig);
        PortConfig portConfig = new PortConfig();
        portConfig.setWebListenerPort(456);
        masterServerConfig.setPorts(portConfig);
        Base64KeyPair base64KeyPair = new Base64KeyPair();
        base64KeyPair.setPubKey("pub key");
        masterServerConfig.setKeyPair(base64KeyPair);
        MasterServerConfig masterServerConfigCopy = masterServerConfig.copy();
        assertEquals(masterServerConfig, masterServerConfigCopy);
        assertNotSame(masterServerConfig, masterServerConfigCopy);
    }
}
