package ru.babobka.nodemasterserver.validation.config.rule;

import ru.babobka.nodemasterserver.server.MasterServerConfig;
import ru.babobka.nodeutils.validation.ValidationRule;

/**
 * Created by 123 on 26.07.2017.
 */
public class WebPortValidationRule implements ValidationRule<MasterServerConfig> {

    private static final int PORT_MIN = 1024;

    private static final int PORT_MAX = 65535;

    @Override
    public void validate(MasterServerConfig data) {
        if (data.getWebListenerPort() <= 0) {
            throw new IllegalArgumentException("'webPort' value must be positive");
        } else if (data.getWebListenerPort() < PORT_MIN || data.getWebListenerPort() > PORT_MAX) {
            throw new IllegalArgumentException("'webPort' must be in range [" + PORT_MIN + ";" + PORT_MAX + "]");
        } else if (data.getWebListenerPort() == data.getSlaveListenerPort()) {
            throw new IllegalArgumentException("'webPort' and 'mainServerPort' must not be equal");
        }
    }
}
