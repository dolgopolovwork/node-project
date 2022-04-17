package ru.babobka.nodeconfigs.slave.validation.rule;

import org.junit.Test;
import ru.babobka.nodeconfigs.slave.SlaveServerConfig;

/**
 * Created by 123 on 02.06.2018.
 */
public class WebServerPortValidationRuleTest {

    private WebServerPortValidationRule webServerPortValidationRule = new WebServerPortValidationRule();

    @Test(expected = IllegalArgumentException.class)
    public void testValidateNegativePort() {
        SlaveServerConfig config = new SlaveServerConfig();
        config.setWebPort(-1);
        webServerPortValidationRule.validate(config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateTooBigPort() {
        SlaveServerConfig config = new SlaveServerConfig();
        config.setWebPort(1_000_000);
        webServerPortValidationRule.validate(config);
    }

    @Test
    public void testValidate() {
        SlaveServerConfig config = new SlaveServerConfig();
        config.setWebPort(1024);
        webServerPortValidationRule.validate(config);
    }
}
