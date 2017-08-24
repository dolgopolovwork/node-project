package ru.babobka.nodeslaveserver.validator.config.rule;

import ru.babobka.nodeslaveserver.server.SlaveServerConfig;
import ru.babobka.nodeutils.validation.ValidationRule;

/**
 * Created by 123 on 05.11.2017.
 */
public class ServerPortValidationRule implements ValidationRule<SlaveServerConfig> {
    @Override
    public void validate(SlaveServerConfig slaveServerConfig) {
        if (slaveServerConfig.getServerPort() < 1024 || slaveServerConfig.getServerPort() > 65535) {
            throw new IllegalArgumentException("server port must be between 1024 and 65535");
        }
    }
}
