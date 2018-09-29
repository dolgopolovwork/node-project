package ru.babobka.nodeconfigs.slave.validation.rule;

import org.junit.Test;
import ru.babobka.nodeconfigs.slave.SlaveServerConfig;
import ru.babobka.nodeconfigs.slave.validation.rule.RequestTimeoutMillisValidationRule;

/**
 * Created by 123 on 02.06.2018.
 */
public class RequestTimeoutMillisValidationRuleTest {

    private RequestTimeoutMillisValidationRule requestTimeoutMillisValidationRule = new RequestTimeoutMillisValidationRule();

    @Test(expected = IllegalArgumentException.class)
    public void testAuthTimeoutMillisValidationRuleNegative() {
        SlaveServerConfig config = new SlaveServerConfig();
        config.setRequestTimeoutMillis(-1);
        requestTimeoutMillisValidationRule.validate(config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAuthTimeoutMillisValidationRuleZero() {
        SlaveServerConfig config = new SlaveServerConfig();
        config.setRequestTimeoutMillis(0);
        requestTimeoutMillisValidationRule.validate(config);
    }

    @Test
    public void testAuthTimeoutMillisValidationRule() {
        SlaveServerConfig config = new SlaveServerConfig();
        config.setRequestTimeoutMillis(10);
        requestTimeoutMillisValidationRule.validate(config);
    }
}
