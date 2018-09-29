package ru.babobka.nodeconfigs.master.validation.rule;

import org.junit.Test;
import ru.babobka.nodeconfigs.master.MasterServerConfig;
import ru.babobka.nodeconfigs.master.SecurityConfig;
import ru.babobka.nodesecurity.rsa.RSAConfigFactory;
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
        securityConfig.setBigSafePrime(SafePrime.random(16).getPrime());
        config.setSecurity(securityConfig);
        securityConfigValidationRule.validate(config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateTooLittleChallenge() {
        MasterServerConfig config = new MasterServerConfig();
        SecurityConfig securityConfig = new SecurityConfig();
        securityConfig.setChallengeBytes(1);
        securityConfig.setBigSafePrime(SafePrime.random(64).getPrime());
        config.setSecurity(securityConfig);
        securityConfigValidationRule.validate(config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateTooBigChallenge() {
        MasterServerConfig config = new MasterServerConfig();
        SecurityConfig securityConfig = new SecurityConfig();
        securityConfig.setChallengeBytes(128);
        securityConfig.setBigSafePrime(SafePrime.random(64).getPrime());
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

    @Test(expected = IllegalArgumentException.class)
    public void testValidateRsaConfigWasNotSet() {
        MasterServerConfig config = new MasterServerConfig();
        SecurityConfig securityConfig = new SecurityConfig();
        securityConfig.setChallengeBytes(8);
        securityConfig.setBigSafePrime(SafePrime.random(64).getPrime());
        securityConfig.setRsaConfig(null);
        config.setSecurity(securityConfig);
        securityConfigValidationRule.validate(config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateTooWeakRsaConfig() {
        MasterServerConfig config = new MasterServerConfig();
        SecurityConfig securityConfig = new SecurityConfig();
        securityConfig.setChallengeBytes(8);
        securityConfig.setBigSafePrime(SafePrime.random(64).getPrime());
        securityConfig.setRsaConfig(RSAConfigFactory.create(8));
        config.setSecurity(securityConfig);
        securityConfigValidationRule.validate(config);
    }

    @Test
    public void testValidate() {
        MasterServerConfig config = new MasterServerConfig();
        SecurityConfig securityConfig = new SecurityConfig();
        securityConfig.setChallengeBytes(8);
        securityConfig.setBigSafePrime(SafePrime.random(64).getPrime());
        securityConfig.setRsaConfig(RSAConfigFactory.create(128));
        config.setSecurity(securityConfig);
        securityConfigValidationRule.validate(config);
    }

}
