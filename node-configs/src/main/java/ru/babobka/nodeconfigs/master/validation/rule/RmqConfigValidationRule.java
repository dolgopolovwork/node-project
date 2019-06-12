package ru.babobka.nodeconfigs.master.validation.rule;

import ru.babobka.nodeconfigs.master.MasterServerConfig;
import ru.babobka.nodeutils.util.TextUtil;
import ru.babobka.nodeutils.validation.ValidationRule;

public class RmqConfigValidationRule implements ValidationRule<MasterServerConfig> {

    @Override
    public void validate(MasterServerConfig masterServerConfig) {
        if (masterServerConfig.getRmqConfig() != null) {
            if (!TextUtil.isValidPort(masterServerConfig.getRmqConfig().getPort())) {
                throw new IllegalArgumentException("invalid Rmq port "
                        + masterServerConfig.getRmqConfig().getPort());
            } else if (TextUtil.isEmpty(masterServerConfig.getRmqConfig().getHost())) {
                throw new IllegalArgumentException("RMQ host was not set");
            }
        }
    }
}
