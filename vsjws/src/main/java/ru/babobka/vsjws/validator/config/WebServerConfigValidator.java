package ru.babobka.vsjws.validator.config;

import ru.babobka.nodeutils.validation.Validator;
import ru.babobka.vsjws.validator.config.rule.PortValidationRule;
import ru.babobka.vsjws.validator.config.rule.ServerNameValidationRule;
import ru.babobka.vsjws.validator.config.rule.SessionTimeoutValidationRule;
import ru.babobka.vsjws.webserver.WebServerConfig;

import java.util.Arrays;

/**
 * Created by 123 on 01.01.2018.
 */
public class WebServerConfigValidator extends Validator<WebServerConfig> {

    public WebServerConfigValidator() {
        super(Arrays.asList(
                new PortValidationRule(),
                new ServerNameValidationRule(),
                new SessionTimeoutValidationRule()));
    }

}
