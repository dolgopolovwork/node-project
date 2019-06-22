package ru.babobka.nodeconfigs.utils;

import org.junit.Test;
import ru.babobka.nodeconfigs.exception.EnvConfigCreationException;
import ru.babobka.nodeconfigs.slave.SlaveServerConfig;
import ru.babobka.nodeconfigs.slave.SlaveServerConfigTest;
import ru.babobka.nodeconfigs.utils.reflection.*;
import ru.babobka.nodeserials.enumerations.ResponseStatus;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class EnvBasedConfigUtilTest {

    @Test
    public void testHasDefaultConstructor() {
        assertTrue(EnvBasedConfigUtil.hasDefaultConstructor(DefaultPublicConstructorClass.class));
    }

    @Test
    public void testHasDefaultConstructorNoArg() {
        assertTrue(EnvBasedConfigUtil.hasDefaultConstructor(NoArgPublicConstructorClass.class));
    }

    @Test
    public void testHasDefaultConstructorPrivate() {
        assertFalse(EnvBasedConfigUtil.hasDefaultConstructor(NoDefaultPublicConstructorClass.class));
    }

    @Test
    public void testHasNoDefaultConstructor() {
        assertFalse(EnvBasedConfigUtil.hasDefaultConstructor(NoDefaultConstructorClass.class));
    }

    @Test
    public void testGetGetterPojo() throws NoSuchFieldException {
        Method getter = EnvBasedConfigUtil.getGetter(PojoClass.class.getDeclaredField("value"), PojoClass.class.getMethods());
        assertEquals(getter.getName(), "getValue");
    }

    @Test
    public void testGetSetterPojo() throws NoSuchFieldException {
        Method setter = EnvBasedConfigUtil.getSetter(PojoClass.class.getDeclaredField("value"), PojoClass.class.getMethods());
        assertEquals(setter.getName(), "setValue");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetGetterNonPojo() throws NoSuchFieldException {
        EnvBasedConfigUtil.getGetter(NonPojoClass.class.getDeclaredField("value"), NonPojoClass.class.getMethods());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetSetterNonPojo() throws NoSuchFieldException {
        EnvBasedConfigUtil.getSetter(NonPojoClass.class.getDeclaredField("value"), NonPojoClass.class.getMethods());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateFieldArray() {
        EnvBasedConfigUtil.validateField(new String[]{"a", "b", "c"}.getClass(), "envValue");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateFieldMap() {
        EnvBasedConfigUtil.validateField(HashMap.class, "envValue");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateFieldList() {
        EnvBasedConfigUtil.validateField(LinkedList.class, "envValue");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateFieldInterface() {
        EnvBasedConfigUtil.validateField(List.class, "envValue");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateFieldEnum() {
        EnvBasedConfigUtil.validateField(ResponseStatus.class, "envValue");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateFieldNoDefaultConstructor() {
        EnvBasedConfigUtil.validateField(NoDefaultConstructorClass.class, "envValue");
    }

    @Test
    public void testValidateField() {
        EnvBasedConfigUtil.validateField(PojoClass.class, "envValue");
    }

    @Test
    public void testHasDesiredEnvVariablesEmptyVars() {
        assertFalse(EnvBasedConfigUtil.hasDesiredEnvVariables("prefix", new HashMap<>()));
    }

    @Test
    public void testHasDesiredEnvVariablesEmptyPrefix() {
        Map<String, String> env = new HashMap<>();
        env.put("123", "abc");
        assertFalse(EnvBasedConfigUtil.hasDesiredEnvVariables("", env));
    }

    @Test
    public void testHasDesiredEnvVariablesNoDesiredValues() {
        Map<String, String> env = new HashMap<>();
        env.put("123", "abc");
        assertFalse(EnvBasedConfigUtil.hasDesiredEnvVariables("456", env));
    }

    @Test
    public void testHasDesiredEnvVariables() {
        Map<String, String> env = new HashMap<>();
        env.put("123456", "abc");
        assertTrue(EnvBasedConfigUtil.hasDesiredEnvVariables("123", env));
    }

    @Test
    public void testBuildFromEnvNoEnv() throws EnvConfigCreationException {
        SlaveServerConfig slaveServerConfig = SlaveServerConfigTest.createRandomSlaveConfig();
        assertEquals(slaveServerConfig, EnvBasedConfigUtil.buildFromEnv(slaveServerConfig, new HashMap<>()));
    }

    @Test
    public void testBuildFromEnvRandomEnv() throws EnvConfigCreationException {
        SlaveServerConfig slaveServerConfig = SlaveServerConfigTest.createRandomSlaveConfig();
        Map<String, String> env = new HashMap<>();
        env.put("test", "123");
        env.put("foo", "456");
        env.put("bar", "789");
        SlaveServerConfig slaveServerConfigEnv = EnvBasedConfigUtil.buildFromEnv(slaveServerConfig, env);
        assertEquals(slaveServerConfig, slaveServerConfigEnv);
        assertNotSame(slaveServerConfig, slaveServerConfigEnv);
    }

    @Test
    public void testBuildFromEnvPort() throws EnvConfigCreationException {
        SlaveServerConfig slaveServerConfig = SlaveServerConfigTest.createRandomSlaveConfig();
        SlaveServerConfig slaveServerConfigCopy = slaveServerConfig.copy();
        Map<String, String> env = new HashMap<>();
        String envPort = "12345";
        env.put("SLAVESERVERCONFIG_MASTERSERVERPORT", envPort);
        SlaveServerConfig slaveServerConfigEnv = EnvBasedConfigUtil.buildFromEnv(slaveServerConfig, env);
        assertNotEquals(slaveServerConfig, slaveServerConfigEnv);
        assertEquals(slaveServerConfigEnv.getMasterServerPort(), (int) Integer.valueOf(envPort));
        assertEquals(slaveServerConfig, slaveServerConfigCopy);
        assertNotSame(slaveServerConfig, slaveServerConfigEnv);
        slaveServerConfigEnv.setMasterServerPort(slaveServerConfig.getMasterServerPort());
        assertEquals(slaveServerConfig, slaveServerConfigEnv);
    }

    @Test
    public void testBuildFromEnvLogin() throws EnvConfigCreationException {
        SlaveServerConfig slaveServerConfig = SlaveServerConfigTest.createRandomSlaveConfig();
        SlaveServerConfig slaveServerConfigCopy = slaveServerConfig.copy();
        Map<String, String> env = new HashMap<>();
        String envLogin = "envLogin";
        env.put("SLAVESERVERCONFIG_SLAVELOGIN", envLogin);
        SlaveServerConfig slaveServerConfigEnv = EnvBasedConfigUtil.buildFromEnv(slaveServerConfig, env);
        assertNotEquals(slaveServerConfig, slaveServerConfigEnv);
        assertEquals(slaveServerConfigEnv.getSlaveLogin(), envLogin);
        assertEquals(slaveServerConfig, slaveServerConfigCopy);
        assertNotSame(slaveServerConfig, slaveServerConfigEnv);
        slaveServerConfigEnv.setSlaveLogin(slaveServerConfig.getSlaveLogin());
        assertEquals(slaveServerConfig, slaveServerConfigEnv);
    }

    @Test(expected = EnvConfigCreationException.class)
    public void testBuildFromEnvInvalidPort() throws EnvConfigCreationException {
        SlaveServerConfig slaveServerConfig = SlaveServerConfigTest.createRandomSlaveConfig();
        Map<String, String> env = new HashMap<>();
        String envPort = "not a number";
        env.put("SLAVESERVERCONFIG_MASTERSERVERPORT", envPort);
        EnvBasedConfigUtil.buildFromEnv(slaveServerConfig, env);
    }

    @Test
    public void testBuildFromEnvKeyPair() throws EnvConfigCreationException {
        SlaveServerConfig slaveServerConfig = SlaveServerConfigTest.createRandomSlaveConfig();
        SlaveServerConfig slaveServerConfigCopy = slaveServerConfig.copy();
        Map<String, String> env = new HashMap<>();
        String envPubKey = "envPubKey";
        String envPrivKey = "envPrivKey";
        env.put("SLAVESERVERCONFIG_KEYPAIR_PUBKEY", envPubKey);
        env.put("SLAVESERVERCONFIG_KEYPAIR_PRIVKEY", envPrivKey);
        SlaveServerConfig slaveServerConfigEnv = EnvBasedConfigUtil.buildFromEnv(slaveServerConfig, env);
        assertNotEquals(slaveServerConfig, slaveServerConfigEnv);
        assertEquals(slaveServerConfigEnv.getKeyPair().getPubKey(), envPubKey);
        assertEquals(slaveServerConfigEnv.getKeyPair().getPrivKey(), envPrivKey);
        assertEquals(slaveServerConfig, slaveServerConfigCopy);
        assertNotSame(slaveServerConfig, slaveServerConfigEnv);
        slaveServerConfigEnv.setKeyPair(slaveServerConfig.getKeyPair());
        assertEquals(slaveServerConfig, slaveServerConfigEnv);
    }

    @Test
    public void testBuildFromEnvKeyPairNullOriginal() throws EnvConfigCreationException {
        SlaveServerConfig slaveServerConfig = SlaveServerConfigTest.createRandomSlaveConfig();
        slaveServerConfig.setKeyPair(null);
        SlaveServerConfig slaveServerConfigCopy = slaveServerConfig.copy();
        Map<String, String> env = new HashMap<>();
        String envPubKey = "envPubKey";
        String envPrivKey = "envPrivKey";
        env.put("SLAVESERVERCONFIG_KEYPAIR_PUBKEY", envPubKey);
        env.put("SLAVESERVERCONFIG_KEYPAIR_PRIVKEY", envPrivKey);
        SlaveServerConfig slaveServerConfigEnv = EnvBasedConfigUtil.buildFromEnv(slaveServerConfig, env);
        assertNotEquals(slaveServerConfig, slaveServerConfigEnv);
        assertEquals(slaveServerConfigEnv.getKeyPair().getPubKey(), envPubKey);
        assertEquals(slaveServerConfigEnv.getKeyPair().getPrivKey(), envPrivKey);
        assertEquals(slaveServerConfig, slaveServerConfigCopy);
        assertNotSame(slaveServerConfig, slaveServerConfigEnv);
        slaveServerConfigEnv.setKeyPair(slaveServerConfig.getKeyPair());
        assertEquals(slaveServerConfig, slaveServerConfigEnv);
    }
}
