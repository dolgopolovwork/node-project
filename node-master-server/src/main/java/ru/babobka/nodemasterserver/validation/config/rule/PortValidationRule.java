package ru.babobka.nodemasterserver.validation.config.rule;

import ru.babobka.nodemasterserver.server.config.MasterServerConfig;
import ru.babobka.nodemasterserver.server.config.PortConfig;
import ru.babobka.nodeutils.util.ArrayUtil;
import ru.babobka.nodeutils.util.TextUtil;
import ru.babobka.nodeutils.validation.ValidationRule;

/**
 * Created by 123 on 13.05.2018.
 */
public class PortValidationRule implements ValidationRule<MasterServerConfig> {
    @Override
    public void validate(MasterServerConfig config) {
        PortConfig portConfig = config.getPorts();
        if (portConfig == null) {
            throw new IllegalArgumentException("portConfig was not set");
        } else if (!TextUtil.isValidPort(portConfig.getWebListenerPort())) {
            throw new IllegalArgumentException("webPort is invalid");
        } else if (!TextUtil.isValidPort(portConfig.getClientListenerPort())) {
            throw new IllegalArgumentException("clientPort is invalid");
        } else if (!TextUtil.isValidPort(portConfig.getSlaveListenerPort())) {
            throw new IllegalArgumentException("slavePort is invalid");
        } else if (!ArrayUtil.isUnique(portConfig.getClientListenerPort(), portConfig.getSlaveListenerPort(), portConfig.getWebListenerPort())) {
            throw new IllegalArgumentException("all the ports must be unique");
        }
    }
}
