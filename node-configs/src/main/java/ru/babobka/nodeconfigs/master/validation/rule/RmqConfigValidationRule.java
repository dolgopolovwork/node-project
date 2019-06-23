package ru.babobka.nodeconfigs.master.validation.rule;

import ru.babobka.nodeconfigs.master.MasterServerConfig;
import ru.babobka.nodeutils.util.TextUtil;
import ru.babobka.nodeutils.validation.ValidationRule;

public class RmqConfigValidationRule implements ValidationRule<MasterServerConfig> {

    @Override
    public void validate(MasterServerConfig masterServerConfig) {
        if (masterServerConfig.getRmq() != null) {
            if (!TextUtil.isValidPort(masterServerConfig.getRmq().getPort())) {
                throw new IllegalArgumentException("invalid Rmq port "
                        + masterServerConfig.getRmq().getPort());
            } else if (TextUtil.isEmpty(masterServerConfig.getRmq().getHost())) {
                throw new IllegalArgumentException("RMQ host was not set");
            }
        }
    }
}
