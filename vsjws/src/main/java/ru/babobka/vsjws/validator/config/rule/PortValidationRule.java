package ru.babobka.vsjws.validator.config.rule;


import ru.babobka.nodeutils.validation.ValidationRule;
import ru.babobka.vsjws.webserver.WebServerConfig;

/**
 * Created by 123 on 01.01.2018.
 */
public class PortValidationRule implements ValidationRule<WebServerConfig> {

    private static final int MAX_PORT = 65536;

    @Override
    public void validate(WebServerConfig config) {
        if (config.getPort() < 0 || config.getPort() > MAX_PORT) {
            throw new IllegalArgumentException("Port must be in range [0;" + MAX_PORT + ")");
        }
    }
}
