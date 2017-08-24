package ru.babobka.nodemasterserver.validation.config.rule;

import ru.babobka.nodemasterserver.server.MasterServerConfig;
import ru.babobka.nodeutils.validation.ValidationRule;

/**
 * Created by 123 on 26.07.2017.
 */
public class AuthTimeoutMillisValidationRule implements ValidationRule<MasterServerConfig> {
    @Override
    public void validate(MasterServerConfig data) {
        if (data.getAuthTimeOutMillis() <= 0) {
            throw new IllegalArgumentException("'authTimeOutMillis' value must be positive");
        }
    }
}
