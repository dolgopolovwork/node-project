package ru.babobka.vsjws.validator.config.rule;


import ru.babobka.nodeutils.util.TextUtil;
import ru.babobka.nodeutils.validation.ValidationRule;
import ru.babobka.vsjws.webserver.WebServerConfig;

/**
 * Created by 123 on 01.01.2018.
 */
public class PortValidationRule implements ValidationRule<WebServerConfig> {

    @Override
    public void validate(WebServerConfig config) {
        if (!TextUtil.isValidPort(config.getPort())) {
            throw new IllegalArgumentException("Invalid port " + config.getPort());
        }
    }
}
