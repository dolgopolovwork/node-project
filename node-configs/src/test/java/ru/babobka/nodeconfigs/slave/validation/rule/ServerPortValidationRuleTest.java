package ru.babobka.nodeconfigs.slave.validation.rule;

import org.junit.Test;
import ru.babobka.nodeconfigs.slave.SlaveServerConfig;

/**
 * Created by 123 on 02.06.2018.
 */
public class ServerPortValidationRuleTest {

    private ServerPortValidationRule serverPortValidationRule = new ServerPortValidationRule();

    @Test(expected = IllegalArgumentException.class)
    public void testValidateNegativePort() {
        SlaveServerConfig config = new SlaveServerConfig();
        config.setServerPort(-1);
        serverPortValidationRule.validate(config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateTooBigPort() {
        SlaveServerConfig config = new SlaveServerConfig();
        config.setServerPort(1_000_000);
        serverPortValidationRule.validate(config);
    }

    @Test
    public void testValidate() {
        SlaveServerConfig config = new SlaveServerConfig();
        config.setServerPort(1024);
        serverPortValidationRule.validate(config);
    }
}
