package ru.babobka.nodeconfigs.slave.validation.rule;

import ru.babobka.nodeconfigs.slave.SlaveServerConfig;
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
