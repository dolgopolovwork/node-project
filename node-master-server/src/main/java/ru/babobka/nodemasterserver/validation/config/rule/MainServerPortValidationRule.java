package ru.babobka.nodemasterserver.validation.config.rule;

import ru.babobka.nodemasterserver.server.MasterServerConfig;
import ru.babobka.nodeutils.validation.ValidationRule;

/**
 * Created by 123 on 26.07.2017.
 */
public class MainServerPortValidationRule implements ValidationRule<MasterServerConfig> {

    private static final int PORT_MIN = 1024;

    private static final int PORT_MAX = 65535;

    @Override
    public void validate(MasterServerConfig data) {
        if (data.getSlaveListenerPort() <= 0) {
            throw new IllegalArgumentException("'mainServerPort' value must be positive");
        } else if (data.getSlaveListenerPort() < PORT_MIN || data.getSlaveListenerPort() > PORT_MAX) {
            throw new IllegalArgumentException(
                    "'mainServerPort' must be in range [" + PORT_MIN + ";" + PORT_MAX + "]");
        }
    }
}
