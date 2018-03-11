package ru.babobka.vsjws.validator.config;

import ru.babobka.vsjws.validator.ValidationRule;
import ru.babobka.vsjws.validator.config.rule.PortValidationRule;
import ru.babobka.vsjws.validator.config.rule.ServerNameValidationRule;
import ru.babobka.vsjws.validator.config.rule.SessionTimeoutValidationRule;
import ru.babobka.vsjws.webserver.WebServerConfig;

import java.util.Arrays;
import java.util.List;

/**
 * Created by 123 on 01.01.2018.
 */
public class WebServerConfigValidator {
    private final List<ValidationRule<WebServerConfig>> rules = Arrays.asList(
            new PortValidationRule(),
            new ServerNameValidationRule(),
            new SessionTimeoutValidationRule());

    public void validate(WebServerConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("config is null");
        }
        for (ValidationRule<WebServerConfig> rule : rules) {
            rule.validate(config);
        }
    }
}
