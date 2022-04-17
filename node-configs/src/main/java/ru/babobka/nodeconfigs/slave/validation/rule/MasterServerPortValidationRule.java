package ru.babobka.nodeconfigs.slave.validation.rule;

import ru.babobka.nodeconfigs.slave.SlaveServerConfig;
import ru.babobka.nodeutils.util.TextUtil;
import ru.babobka.nodeutils.validation.ValidationRule;

/**
 * Created by 123 on 05.11.2017.
 */
public class MasterServerPortValidationRule implements ValidationRule<SlaveServerConfig> {
    @Override
    public void validate(SlaveServerConfig slaveServerConfig) {
        if (!TextUtil.isValidPort(slaveServerConfig.getMasterServerPort())) {
            throw new IllegalArgumentException("invalid server port " + slaveServerConfig.getMasterServerPort());
        }
    }
}
