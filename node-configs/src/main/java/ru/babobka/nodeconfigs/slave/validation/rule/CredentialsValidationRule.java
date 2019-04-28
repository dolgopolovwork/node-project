package ru.babobka.nodeconfigs.slave.validation.rule;

import ru.babobka.nodeconfigs.slave.SlaveServerConfig;
import ru.babobka.nodeutils.util.TextUtil;
import ru.babobka.nodeutils.validation.ValidationRule;

public class CredentialsValidationRule implements ValidationRule<SlaveServerConfig> {
    @Override
    public void validate(SlaveServerConfig config) {
        if (TextUtil.isEmpty(config.getSlaveLogin())) {
            throw new IllegalArgumentException("Slave server login was not set");
        } else if (TextUtil.isEmpty(config.getSlavePassword())) {
            throw new IllegalArgumentException("Slave server password was not set");
        }
    }
}
