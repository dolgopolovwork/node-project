package ru.babobka.nodeconfigs.master.validation.rule;

import ru.babobka.nodeconfigs.master.MasterServerConfig;
import ru.babobka.nodeconfigs.master.TimeConfig;
import ru.babobka.nodeutils.validation.ValidationRule;

/**
 * Created by 123 on 13.05.2018.
 */
public class TimeValidationRule implements ValidationRule<MasterServerConfig> {
    @Override
    public void validate(MasterServerConfig config) {
        TimeConfig timeConfig = config.getTime();
        if (timeConfig == null) {
            throw new IllegalArgumentException("timeConfig was not set");
        } else if (timeConfig.getRequestReadTimeOutMillis() <= 0) {
            throw new IllegalArgumentException("'requestReadTimeOutMillis' value must be positive");
        } else if (timeConfig.getAuthTimeOutMillis() <= 0) {
            throw new IllegalArgumentException("'authTimeOutMillis' value must be positive");
        } else if (timeConfig.getHeartBeatCycleMillis() <= 0) {
            throw new IllegalArgumentException("'heartBeatCycleMillis' value must be positive");
        } else if (timeConfig.getHeartBeatCycleMillis() >= timeConfig.getRequestReadTimeOutMillis()) {
            throw new IllegalArgumentException(
                    "'heartBeatTimeOutMillis' value must lower than 'requestTimeOutMillis'");
        } else if (timeConfig.getDataOutDateMillis() <= 0) {
            throw new IllegalArgumentException("'dataOutDateMillis' value must be positive");
        }

    }
}
