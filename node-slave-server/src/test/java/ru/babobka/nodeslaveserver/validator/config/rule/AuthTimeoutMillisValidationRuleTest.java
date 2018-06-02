package ru.babobka.nodeslaveserver.validator.config.rule;

import org.junit.Test;
import ru.babobka.nodeslaveserver.server.SlaveServerConfig;

/**
 * Created by 123 on 02.06.2018.
 */
public class AuthTimeoutMillisValidationRuleTest {

    private AuthTimeoutMillisValidationRule authTimeoutMillisValidationRule = new AuthTimeoutMillisValidationRule();

    @Test(expected = IllegalArgumentException.class)
    public void testAuthTimeoutMillisValidationRuleNegative() {
        SlaveServerConfig config = new SlaveServerConfig();
        config.setAuthTimeoutMillis(-1);
        authTimeoutMillisValidationRule.validate(config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAuthTimeoutMillisValidationRuleZero() {
        SlaveServerConfig config = new SlaveServerConfig();
        config.setAuthTimeoutMillis(0);
        authTimeoutMillisValidationRule.validate(config);
    }

    @Test
    public void testAuthTimeoutMillisValidationRule() {
        SlaveServerConfig config = new SlaveServerConfig();
        config.setAuthTimeoutMillis(10);
        authTimeoutMillisValidationRule.validate(config);
    }
}
