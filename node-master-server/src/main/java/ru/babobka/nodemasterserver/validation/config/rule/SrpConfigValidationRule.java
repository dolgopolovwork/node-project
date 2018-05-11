package ru.babobka.nodemasterserver.validation.config.rule;

import ru.babobka.nodemasterserver.server.MasterServerConfig;
import ru.babobka.nodeutils.validation.ValidationRule;

/**
 * Created by 123 on 04.05.2018.
 */
public class SrpConfigValidationRule implements ValidationRule<MasterServerConfig> {
    private static final int MIN_PRIME_BITS = 64;
    private static final int MIN_CHALLENGE_BYTES = 8;
    private static final int MAX_CHALLENGE_BYTES = 64;

    //TODO напиши на это тесты
    @Override
    public void validate(MasterServerConfig config) {
        if (config.getBigSafePrime() == null) {
            throw new IllegalArgumentException("bigSafePrime was not set");
        } else if (config.getBigSafePrime().getPrime().bitLength() < MIN_PRIME_BITS) {
            throw new IllegalArgumentException("bigSafePrime is not bit enough. must be at least " + MIN_PRIME_BITS + " length");
        } else if (config.getChallengeBytes() < MIN_CHALLENGE_BYTES || config.getChallengeBytes() > MAX_CHALLENGE_BYTES) {
            throw new IllegalArgumentException("challengeBytes must be in range [" + MIN_CHALLENGE_BYTES + ";" + MAX_CHALLENGE_BYTES + "]");
        }
    }
}
