package ru.babobka.nodemasterserver.validation.config.rule;

import org.junit.Test;
import ru.babobka.nodemasterserver.server.config.MasterServerConfig;
import ru.babobka.nodemasterserver.server.config.SecurityConfig;
import ru.babobka.nodemasterserver.validation.config.rule.SecurityConfigValidationRule;
import ru.babobka.nodeutils.math.SafePrime;

/**
 * Created by 123 on 13.05.2018.
 */
public class SecurityConfigValidationRuleTest {
    private SecurityConfigValidationRule securityConfigValidationRule = new SecurityConfigValidationRule();

    @Test(expected = IllegalArgumentException.class)
    public void testValidateNullConfig() {
        MasterServerConfig config = new MasterServerConfig();
        config.setSecurity(null);
        securityConfigValidationRule.validate(config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateLittlePrime() {
        MasterServerConfig config = new MasterServerConfig();
        SecurityConfig securityConfig = new SecurityConfig();
        securityConfig.setChallengeBytes(10);
        securityConfig.setBigSafePrime(SafePrime.random(16));
        config.setSecurity(securityConfig);
        securityConfigValidationRule.validate(config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateTooLittleChallenge() {
        MasterServerConfig config = new MasterServerConfig();
        SecurityConfig securityConfig = new SecurityConfig();
        securityConfig.setChallengeBytes(1);
        securityConfig.setBigSafePrime(SafePrime.random(64));
        config.setSecurity(securityConfig);
        securityConfigValidationRule.validate(config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateTooBigChallenge() {
        MasterServerConfig config = new MasterServerConfig();
        SecurityConfig securityConfig = new SecurityConfig();
        securityConfig.setChallengeBytes(128);
        securityConfig.setBigSafePrime(SafePrime.random(64));
        config.setSecurity(securityConfig);
        securityConfigValidationRule.validate(config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateNoSafePrime() {
        MasterServerConfig config = new MasterServerConfig();
        SecurityConfig securityConfig = new SecurityConfig();
        securityConfig.setChallengeBytes(8);
        securityConfig.setBigSafePrime(null);
        config.setSecurity(securityConfig);
        securityConfigValidationRule.validate(config);
    }

    @Test
    public void testValidate() {
        MasterServerConfig config = new MasterServerConfig();
        SecurityConfig securityConfig = new SecurityConfig();
        securityConfig.setChallengeBytes(8);
        securityConfig.setBigSafePrime(SafePrime.random(64));
        config.setSecurity(securityConfig);
        securityConfigValidationRule.validate(config);
    }

}
