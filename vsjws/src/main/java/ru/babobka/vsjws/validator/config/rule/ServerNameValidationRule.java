package ru.babobka.vsjws.validator.config.rule;

import ru.babobka.vsjws.enumerations.RegularExpressions;
import ru.babobka.vsjws.validator.ValidationRule;
import ru.babobka.vsjws.webserver.WebServerConfig;

/**
 * Created by 123 on 01.01.2018.
 */
public class ServerNameValidationRule implements ValidationRule<WebServerConfig> {
    @Override
    public void validate(WebServerConfig config) {
        if (config.getServerName() == null || config.getServerName().isEmpty()) {
            throw new IllegalArgumentException("Web server name was no set");
        } else if (!config.getServerName().matches(RegularExpressions.FILE_NAME_PATTERN.toString())) {
            throw new IllegalArgumentException("Web server name must contain letters,numbers and spaces only");
        }
    }
}