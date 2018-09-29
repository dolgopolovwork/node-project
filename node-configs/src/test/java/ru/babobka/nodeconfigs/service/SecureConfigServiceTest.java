package ru.babobka.nodeconfigs.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import ru.babobka.nodeconfigs.NodeConfiguration;
import ru.babobka.nodesecurity.service.SecureJSONService;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.util.StreamUtil;

import java.io.IOException;

import static org.junit.Assert.assertEquals;


/**
 * Created by 123 on 11.09.2018.
 */
public class SecureConfigServiceTest {

    private SecureJSONService secureJSONService;
    private StreamUtil streamUtil;
    private SecureConfigService secureConfigService;

    @Before
    public void setUp() {
        secureJSONService = Mockito.mock(SecureJSONService.class);
        streamUtil = Mockito.mock(StreamUtil.class);
        Container.getInstance().put(container -> {
            container.put(secureJSONService);
            container.put(streamUtil);
        });
        secureConfigService = new SecureConfigService();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetConfigNoPassword() throws IOException {
        secureConfigService.getConfig("/", NodeConfiguration.class, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetConfigNullPassword() throws IOException {
        secureConfigService.getConfig("/", NodeConfiguration.class, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetConfigNoConfigPath() throws IOException {
        secureConfigService.getConfig("", NodeConfiguration.class, "abc");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetConfigNullConfigPath() throws IOException {
        secureConfigService.getConfig(null, NodeConfiguration.class, "abc");
    }

    @Test(expected = IOException.class)
    public void testGetConfigCannotReadFile() throws IOException {
        String configPath = "/";
        Mockito.when(streamUtil.readBytesFromFile(configPath)).thenThrow(new IOException());
        secureConfigService.getConfig(configPath, NodeConfiguration.class, "abc");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetConfigFileIsEmpty() throws IOException {
        String configPath = "/";
        Mockito.when(streamUtil.readBytesFromFile(configPath)).thenReturn(new byte[]{});
        secureConfigService.getConfig(configPath, NodeConfiguration.class, "abc");
    }

    @Test
    public void testGetConfig() throws IOException {
        byte[] fileContent = {1, 2, 3};
        String configPath = "/";
        String password = "abc";
        Class<NodeConfiguration> configurationClass = NodeConfiguration.class;
        Mockito.when(streamUtil.readBytesFromFile(configPath)).thenReturn(fileContent);
        NodeConfiguration configuration = Mockito.mock(NodeConfiguration.class);
        Mockito.when(secureJSONService.decrypt(fileContent, password, configurationClass)).thenReturn(configuration);
        assertEquals(configuration, secureConfigService.getConfig(configPath, configurationClass, password));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateConfigNoPassword() throws IOException {
        NodeConfiguration configuration = Mockito.mock(NodeConfiguration.class);
        secureConfigService.createConfig("/", configuration, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateConfigNullPassword() throws IOException {
        NodeConfiguration configuration = Mockito.mock(NodeConfiguration.class);
        secureConfigService.createConfig("/", configuration, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateConfigNullConfigPath() throws IOException {
        NodeConfiguration configuration = Mockito.mock(NodeConfiguration.class);
        secureConfigService.createConfig(null, configuration, "abc");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateConfigNoConfigPath() throws IOException {
        NodeConfiguration configuration = Mockito.mock(NodeConfiguration.class);
        secureConfigService.createConfig("", configuration, "abc");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateConfigNullConfig() throws IOException {
        secureConfigService.createConfig("/", null, "abc");
    }

    @Test(expected = IOException.class)
    public void testCreateConfigCannotWriteFile() throws IOException {
        byte[] encryptedContent = {1, 2, 3};
        String configPath = "/";
        String password = "abc";
        NodeConfiguration configuration = Mockito.mock(NodeConfiguration.class);
        Mockito.when(secureJSONService.encrypt(configuration, password)).thenReturn(encryptedContent);
        Mockito.doThrow(new IOException()).when(streamUtil).writeBytesToFile(encryptedContent, configPath);
        secureConfigService.createConfig(configPath, configuration, password);
    }

    @Test
    public void testCreateConfig() throws IOException {
        byte[] encryptedContent = {1, 2, 3};
        String configPath = "/";
        String password = "abc";
        NodeConfiguration configuration = Mockito.mock(NodeConfiguration.class);
        Mockito.when(secureJSONService.encrypt(configuration, password)).thenReturn(encryptedContent);
        secureConfigService.createConfig(configPath, configuration, password);
        Mockito.verify(streamUtil).writeBytesToFile(encryptedContent, configPath);
    }
}
