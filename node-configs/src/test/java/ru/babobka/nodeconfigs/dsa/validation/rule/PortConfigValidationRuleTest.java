package ru.babobka.nodeconfigs.dsa.validation.rule;

import org.junit.Test;
import ru.babobka.nodeconfigs.dsa.DSAServerConfig;

public class PortConfigValidationRuleTest {

    private final PortConfigValidationRule rule = new PortConfigValidationRule();

    @Test(expected = IllegalArgumentException.class)
    public void testValidateNegativePort() {
        DSAServerConfig dsaServerConfig = new DSAServerConfig();
        dsaServerConfig.setPort(-1234);
        dsaServerConfig.setLoggerFolder("/logger");
        dsaServerConfig.setKeyPair(null);
        rule.validate(dsaServerConfig);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateOutOfBoundsPort() {
        DSAServerConfig dsaServerConfig = new DSAServerConfig();
        dsaServerConfig.setPort(1234_1234);
        dsaServerConfig.setLoggerFolder("/logger");
        dsaServerConfig.setKeyPair(null);
        rule.validate(dsaServerConfig);
    }

    @Test
    public void testValidateOk() {
        DSAServerConfig dsaServerConfig = new DSAServerConfig();
        dsaServerConfig.setPort(1234);
        dsaServerConfig.setLoggerFolder("/logger");
        dsaServerConfig.setKeyPair(null);
        rule.validate(dsaServerConfig);
    }
}
