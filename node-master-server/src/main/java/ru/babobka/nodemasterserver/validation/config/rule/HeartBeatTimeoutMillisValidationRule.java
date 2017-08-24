package ru.babobka.nodemasterserver.validation.config.rule;

import ru.babobka.nodemasterserver.server.MasterServerConfig;
import ru.babobka.nodeutils.validation.ValidationRule;

/**
 * Created by 123 on 26.07.2017.
 */
public class HeartBeatTimeoutMillisValidationRule implements ValidationRule<MasterServerConfig> {
    @Override
    public void validate(MasterServerConfig data) {
        if (data.getHeartBeatTimeOutMillis() <= 0) {
            throw new IllegalArgumentException("'heartBeatTimeOutMillis' value must be positive");
        } else if (data.getHeartBeatTimeOutMillis() >= data.getRequestTimeOutMillis()) {
            throw new IllegalArgumentException(
                    "'heartBeatTimeOutMillis' value must lower than 'requestTimeOutMillis'");
        }
    }
}
