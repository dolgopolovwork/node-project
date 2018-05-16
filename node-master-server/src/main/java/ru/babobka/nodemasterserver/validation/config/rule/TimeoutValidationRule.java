package ru.babobka.nodemasterserver.validation.config.rule;

import ru.babobka.nodemasterserver.server.config.MasterServerConfig;
import ru.babobka.nodemasterserver.server.config.TimeoutConfig;
import ru.babobka.nodeutils.validation.ValidationRule;

/**
 * Created by 123 on 13.05.2018.
 */
public class TimeoutValidationRule implements ValidationRule<MasterServerConfig> {
    @Override
    public void validate(MasterServerConfig config) {
        TimeoutConfig timeoutConfig = config.getTimeouts();
        if (timeoutConfig == null) {
            throw new IllegalArgumentException("timeoutConfig was not set");
        } else if (timeoutConfig.getRequestTimeOutMillis() <= 0) {
            throw new IllegalArgumentException("'requestTimeOutMillis' value must be positive");
        } else if (timeoutConfig.getAuthTimeOutMillis() <= 0) {
            throw new IllegalArgumentException("'authTimeOutMillis' value must be positive");
        } else if (timeoutConfig.getHeartBeatTimeOutMillis() <= 0) {
            throw new IllegalArgumentException("'heartBeatTimeOutMillis' value must be positive");
        } else if (timeoutConfig.getHeartBeatTimeOutMillis() >= timeoutConfig.getRequestTimeOutMillis()) {
            throw new IllegalArgumentException(
                    "'heartBeatTimeOutMillis' value must lower than 'requestTimeOutMillis'");
        }
    }
}
