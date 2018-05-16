package ru.babobka.nodemasterserver.validation.config.rule;

import org.junit.Test;
import ru.babobka.nodemasterserver.server.config.MasterServerConfig;
import ru.babobka.nodemasterserver.server.config.PortConfig;

/**
 * Created by 123 on 13.05.2018.
 */
public class PortValidationRuleTest {

    private PortValidationRule portValidationRule = new PortValidationRule();

    @Test(expected = IllegalArgumentException.class)
    public void testValidateNoConfig() {
        MasterServerConfig config = new MasterServerConfig();
        config.setPorts(null);
        portValidationRule.validate(config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateInvalidWebPort() {
        MasterServerConfig config = new MasterServerConfig();
        PortConfig portConfig = new PortConfig();
        portConfig.setClientListenerPort(1);
        portConfig.setSlaveListenerPort(2);
        portConfig.setWebListenerPort(-1);
        config.setPorts(portConfig);
        portValidationRule.validate(config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateInvalidSlavePort() {
        MasterServerConfig config = new MasterServerConfig();
        PortConfig portConfig = new PortConfig();
        portConfig.setClientListenerPort(1);
        portConfig.setSlaveListenerPort(-1);
        portConfig.setWebListenerPort(2);
        config.setPorts(portConfig);
        portValidationRule.validate(config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateInvalidClientPort() {
        MasterServerConfig config = new MasterServerConfig();
        PortConfig portConfig = new PortConfig();
        portConfig.setClientListenerPort(-1);
        portConfig.setSlaveListenerPort(1);
        portConfig.setWebListenerPort(2);
        config.setPorts(portConfig);
        portValidationRule.validate(config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateNotUniquePort() {
        MasterServerConfig config = new MasterServerConfig();
        PortConfig portConfig = new PortConfig();
        portConfig.setClientListenerPort(1);
        portConfig.setSlaveListenerPort(1);
        portConfig.setWebListenerPort(2);
        config.setPorts(portConfig);
        portValidationRule.validate(config);
    }

    @Test
    public void testValidate() {
        MasterServerConfig config = new MasterServerConfig();
        PortConfig portConfig = new PortConfig();
        portConfig.setClientListenerPort(1);
        portConfig.setSlaveListenerPort(2);
        portConfig.setWebListenerPort(3);
        config.setPorts(portConfig);
        portValidationRule.validate(config);
    }
}
