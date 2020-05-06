package ru.babobka.nodeconfigs.dsa.validation.rule;

import ru.babobka.nodeconfigs.dsa.DSAServerConfig;
import ru.babobka.nodeutils.util.TextUtil;
import ru.babobka.nodeutils.validation.ValidationRule;

public class PortConfigValidationRule implements ValidationRule<DSAServerConfig> {
    @Override
    public void validate(DSAServerConfig config) {
        if (!TextUtil.isValidPort(config.getPort())) {
            throw new IllegalArgumentException("port is invalid");
        }
    }
}
