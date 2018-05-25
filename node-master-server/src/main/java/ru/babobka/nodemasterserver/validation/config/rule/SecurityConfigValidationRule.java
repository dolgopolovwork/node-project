package ru.babobka.nodemasterserver.validation.config.rule;

import ru.babobka.nodemasterserver.server.config.MasterServerConfig;
import ru.babobka.nodemasterserver.server.config.SecurityConfig;
import ru.babobka.nodeutils.validation.ValidationRule;

/**
 * Created by 123 on 04.05.2018.
 */
public class SecurityConfigValidationRule implements ValidationRule<MasterServerConfig> {
    private static final int MIN_PRIME_BITS = 64;
    private static final int MIN_CHALLENGE_BYTES = 8;
    private static final int MAX_CHALLENGE_BYTES = 64;
    private static final int MIN_RSA_MODULUS_BITS = 128;

    @Override
    public void validate(MasterServerConfig config) {
        SecurityConfig securityConfig = config.getSecurity();
        if (securityConfig == null) {
            throw new IllegalArgumentException("securityConfig was not set");
        } else if (securityConfig.getBigSafePrime() == null) {
            throw new IllegalArgumentException("bigSafePrime was not set");
        } else if (securityConfig.getBigSafePrime().getPrime().bitLength() < MIN_PRIME_BITS) {
            throw new IllegalArgumentException("bigSafePrime is not bit enough. must be at least " + MIN_PRIME_BITS + " length");
        } else if (securityConfig.getChallengeBytes() < MIN_CHALLENGE_BYTES || securityConfig.getChallengeBytes() > MAX_CHALLENGE_BYTES) {
            throw new IllegalArgumentException("challengeBytes must be in range [" + MIN_CHALLENGE_BYTES + ";" + MAX_CHALLENGE_BYTES + "]");
        } else if (securityConfig.getRsaConfig() == null) {
            throw new IllegalArgumentException("rsaConfig was not set");
        } else if (securityConfig.getRsaConfig().getPrivateKey().getN().bitLength() < MIN_RSA_MODULUS_BITS) {
            throw new IllegalArgumentException("rsa modulus must be at least " + MIN_RSA_MODULUS_BITS + " bit length");
        }
    }
}
