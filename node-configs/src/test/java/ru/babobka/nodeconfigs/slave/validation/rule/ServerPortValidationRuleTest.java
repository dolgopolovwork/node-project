package ru.babobka.nodeconfigs.slave.validation.rule;

import org.junit.Test;
import ru.babobka.nodeconfigs.slave.SlaveServerConfig;

/**
 * Created by 123 on 02.06.2018.
 */
public class ServerPortValidationRuleTest {

    private MasterServerPortValidationRule serverPortValidationRule = new MasterServerPortValidationRule();

    @Test(expected = IllegalArgumentException.class)
    public void testValidateNegativePort() {
        SlaveServerConfig config = new SlaveServerConfig();
        config.setMasterServerPort(-1);
        serverPortValidationRule.validate(config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateTooBigPort() {
        SlaveServerConfig config = new SlaveServerConfig();
        config.setMasterServerPort(1_000_000);
        serverPortValidationRule.validate(config);
    }

    @Test
    public void testValidate() {
        SlaveServerConfig config = new SlaveServerConfig();
        config.setMasterServerPort(1024);
        serverPortValidationRule.validate(config);
    }
}
