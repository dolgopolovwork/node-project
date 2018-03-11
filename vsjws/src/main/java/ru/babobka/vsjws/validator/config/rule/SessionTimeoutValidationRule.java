package ru.babobka.vsjws.validator.config.rule;

import ru.babobka.vsjws.validator.ValidationRule;
import ru.babobka.vsjws.webserver.WebServerConfig;

/**
 * Created by 123 on 01.01.2018.
 */
public class SessionTimeoutValidationRule implements ValidationRule<WebServerConfig> {
    @Override
    public void validate(WebServerConfig config) {
        if (config.getSessionTimeoutSeconds() < 0) {
            throw new IllegalArgumentException("Session time out must be > 0");
        }
    }
}
