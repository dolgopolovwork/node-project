package ru.babobka.nodemasterserver.validation.config.rule;

import ru.babobka.nodemasterserver.server.MasterServerConfig;
import ru.babobka.nodeutils.validation.ValidationRule;

/**
 * Created by 123 on 26.07.2017.
 */
public class RequestTimeoutMillisValidationRule implements ValidationRule<MasterServerConfig> {
    @Override
    public void validate(MasterServerConfig data) {
        if (data.getRequestTimeOutMillis() <= 0) {
            throw new IllegalArgumentException("'requestTimeOutMillis' value must be positive");
        }
    }
}