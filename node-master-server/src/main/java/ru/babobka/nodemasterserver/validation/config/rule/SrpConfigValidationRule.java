package ru.babobka.nodemasterserver.validation.config.rule;

import ru.babobka.nodemasterserver.server.MasterServerConfig;
import ru.babobka.nodeutils.validation.ValidationRule;

/**
 * Created by 123 on 04.05.2018.
 */
public class SrpConfigValidationRule implements ValidationRule<MasterServerConfig> {
    private static final int MIN_BITS = 64;

    @Override
    public void validate(MasterServerConfig config) {
        if (config.getBigSafePrime() == null) {
            throw new IllegalArgumentException("bigSafePrime was not set");
        } else if (config.getBigSafePrime().getPrime().bitLength() < MIN_BITS) {
            throw new IllegalArgumentException("bigSafePrime is not bit enough. must be at least " + MIN_BITS + " length");
        }
    }
}
