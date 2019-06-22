package ru.babobka.nodeconfigs.slave;

import org.junit.Test;
import ru.babobka.nodesecurity.keypair.Base64KeyPair;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

public class SlaveServerConfigTest {

    public static SlaveServerConfig createRandomSlaveConfig() {
        SlaveServerConfig slaveServerConfig = new SlaveServerConfig();
        slaveServerConfig.setMasterServerHost("host");
        slaveServerConfig.setMasterServerPort(123);
        slaveServerConfig.setLoggerFolder("abc");
        slaveServerConfig.setTasksFolder("xyz");
        slaveServerConfig.setRequestTimeoutMillis(456);
        slaveServerConfig.setAuthTimeOutMillis(789);
        slaveServerConfig.setMasterServerBase64PublicKey("public key");
        slaveServerConfig.setSlaveLogin("login");
        Base64KeyPair base64KeyPair = new Base64KeyPair();
        base64KeyPair.setPrivKey("private key");
        slaveServerConfig.setKeyPair(base64KeyPair);
        return slaveServerConfig;
    }

    @Test
    public void testCopyDefaultValues() {
        SlaveServerConfig slaveServerConfig = new SlaveServerConfig();
        SlaveServerConfig slaveServerConfigCopy = slaveServerConfig.copy();
        assertEquals(slaveServerConfig, slaveServerConfigCopy);
        assertNotSame(slaveServerConfig, slaveServerConfigCopy);
    }

    @Test
    public void testCopyDefaultSubValues() {
        SlaveServerConfig slaveServerConfig = new SlaveServerConfig();
        slaveServerConfig.setKeyPair(new Base64KeyPair());
        SlaveServerConfig slaveServerConfigCopy = slaveServerConfig.copy();
        assertEquals(slaveServerConfig, slaveServerConfigCopy);
        assertNotSame(slaveServerConfig, slaveServerConfigCopy);
    }

    @Test
    public void testCopyDefaultRandomValues() {
        SlaveServerConfig slaveServerConfig = createRandomSlaveConfig();
        SlaveServerConfig slaveServerConfigCopy = slaveServerConfig.copy();
        assertEquals(slaveServerConfig, slaveServerConfigCopy);
        assertNotSame(slaveServerConfig, slaveServerConfigCopy);
    }

}
