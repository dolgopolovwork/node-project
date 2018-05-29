package ru.babobka.nodeslaveserver.validator.config.rule;

import ru.babobka.nodeslaveserver.server.SlaveServerConfig;
import ru.babobka.nodeutils.util.TextUtil;
import ru.babobka.nodeutils.validation.ValidationRule;

/**
 * Created by 123 on 05.11.2017.
 */
public class ServerPortValidationRule implements ValidationRule<SlaveServerConfig> {
    @Override
    public void validate(SlaveServerConfig slaveServerConfig) {
        if (!TextUtil.isValidPort(slaveServerConfig.getServerPort())) {
            throw new IllegalArgumentException("invalid server port " + slaveServerConfig.getServerPort());
        }
    }
}
