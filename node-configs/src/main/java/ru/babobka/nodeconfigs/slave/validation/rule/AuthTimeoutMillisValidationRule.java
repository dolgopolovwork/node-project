package ru.babobka.nodeconfigs.slave.validation.rule;

import ru.babobka.nodeconfigs.slave.SlaveServerConfig;
import ru.babobka.nodeutils.validation.ValidationRule;

/**
 * Created by 123 on 03.09.2017.
 */
public class AuthTimeoutMillisValidationRule implements ValidationRule<SlaveServerConfig> {
    @Override
    public void validate(SlaveServerConfig data) {
        if (data.getAuthTimeOutMillis() <= 0) {
            throw new IllegalArgumentException("'authTimeoutMillis' value must be natural number");
        }
    }
}