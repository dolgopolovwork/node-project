package ru.babobka.nodeslaveserver.validator.config.rule;

import ru.babobka.nodeslaveserver.server.SlaveServerConfig;
import ru.babobka.nodeutils.validation.ValidationRule;

/**
 * Created by 123 on 26.05.2018.
 */
public class ServerPublicKeyValidationRule implements ValidationRule<SlaveServerConfig> {
    @Override
    public void validate(SlaveServerConfig slaveServerConfig) {
        if (slaveServerConfig.getServerPublicKey() == null) {
            throw new IllegalArgumentException("server public key was not set");
        }
    }
}
