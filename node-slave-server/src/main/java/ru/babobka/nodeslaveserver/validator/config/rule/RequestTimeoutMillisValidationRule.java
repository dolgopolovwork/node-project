package ru.babobka.nodeslaveserver.validator.config.rule;

import ru.babobka.nodeslaveserver.server.SlaveServerConfig;
import ru.babobka.nodeutils.validation.ValidationRule;

/**
 * Created by 123 on 03.09.2017.
 */
public class RequestTimeoutMillisValidationRule implements ValidationRule<SlaveServerConfig> {
    @Override
    public void validate(SlaveServerConfig data) {
        if (data.getRequestTimeoutMillis() <= 0) {
            throw new IllegalArgumentException("'requestTimeoutMillis' value must be positive");
        }
    }
}
