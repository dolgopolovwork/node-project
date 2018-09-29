package ru.babobka.nodeconfigs.slave.validation.rule;

import org.junit.Test;
import ru.babobka.nodeconfigs.slave.SlaveServerConfig;

/**
 * Created by 123 on 02.06.2018.
 */
public class AuthTimeoutMillisValidationRuleTest {

    private AuthTimeoutMillisValidationRule authTimeoutMillisValidationRule = new AuthTimeoutMillisValidationRule();

    @Test(expected = IllegalArgumentException.class)
    public void testAuthTimeoutMillisValidationRuleNegative() {
        SlaveServerConfig config = new SlaveServerConfig();
        config.setAuthTimeOutMillis(-1);
        authTimeoutMillisValidationRule.validate(config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAuthTimeoutMillisValidationRuleZero() {
        SlaveServerConfig config = new SlaveServerConfig();
        config.setAuthTimeOutMillis(0);
        authTimeoutMillisValidationRule.validate(config);
    }

    @Test
    public void testAuthTimeoutMillisValidationRule() {
        SlaveServerConfig config = new SlaveServerConfig();
        config.setAuthTimeOutMillis(10);
        authTimeoutMillisValidationRule.validate(config);
    }
}
