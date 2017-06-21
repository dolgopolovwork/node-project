package ru.babobka.nodeslaveserver.server;

import org.junit.Test;
import ru.babobka.nodeslaveserver.exception.ServerConfigurationException;

import static org.junit.Assert.fail;

/**
 * Created by 123 on 20.06.2017.
 */
public class SlaveServerConfigTest {


    @Test
    public void testValidationBadRequestTimeMillis() {
        try {
            new SlaveServerConfig(-1, -1, -1, null,
                    null);
            fail();
        } catch (ServerConfigurationException e) {

        }
    }

    @Test
    public void testValidationBadAuthTimeMillis() {
        try {
            new SlaveServerConfig(1, -1, -1, null,
                    null);
            fail();
        } catch (ServerConfigurationException e) {

        }
    }

    @Test
    public void testValidationBadThreads() {
        try {
            new SlaveServerConfig(1, 1, -1, null,
                    null);
            fail();
        } catch (ServerConfigurationException e) {

        }
    }

    @Test
    public void testValidationBadMaxThreads() {
        try {
            new SlaveServerConfig(1, 1, 1024, null,
                    null);
            fail();
        } catch (ServerConfigurationException e) {

        }
    }

    @Test
    public void testValidationNullLogFolder() {
        try {
            new SlaveServerConfig(1, 1, 1, null,
                    null);
            fail();
        } catch (ServerConfigurationException e) {

        }
    }


    @Test
    public void testValidationNullTaskFolder() {
        try {
            new SlaveServerConfig(1, 1, 1, "/",
                    null);
            fail();
        } catch (ServerConfigurationException e) {

        }
    }


    @Test
    public void testValidationOk() {
        new SlaveServerConfig(1, 1, 1, "/",
                "/");
    }
}
