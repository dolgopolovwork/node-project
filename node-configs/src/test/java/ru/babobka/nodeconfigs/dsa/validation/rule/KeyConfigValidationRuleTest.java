package ru.babobka.nodeconfigs.dsa.validation.rule;

import org.junit.Test;
import ru.babobka.nodeconfigs.dsa.DSAServerConfig;
import ru.babobka.nodesecurity.keypair.Base64KeyPair;
import ru.babobka.nodesecurity.keypair.KeyDecoder;
import ru.babobka.nodeutils.util.TextUtil;

import java.security.KeyPair;

public class KeyConfigValidationRuleTest {
    private KeyConfigValidationRule rule = new KeyConfigValidationRule();

    @Test(expected = IllegalArgumentException.class)
    public void testValidateNullKeyPair() {
        DSAServerConfig dsaServerConfig = new DSAServerConfig();
        dsaServerConfig.setPort(1234);
        dsaServerConfig.setLoggerFolder("/logger");
        dsaServerConfig.setKeyPair(null);
        rule.validate(dsaServerConfig);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateNullPubKey() {
        KeyPair keyPair = KeyDecoder.generateKeyPair();
        Base64KeyPair invalidKey = new Base64KeyPair();
        invalidKey.setPubKey(null);
        invalidKey.setPrivKey(TextUtil.toBase64(keyPair.getPrivate().getEncoded()));

        DSAServerConfig dsaServerConfig = new DSAServerConfig();
        dsaServerConfig.setPort(1234);
        dsaServerConfig.setLoggerFolder("/logger");
        dsaServerConfig.setKeyPair(invalidKey);
        rule.validate(dsaServerConfig);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateNullPrivKey() {
        KeyPair keyPair = KeyDecoder.generateKeyPair();
        Base64KeyPair invalidKey = new Base64KeyPair();
        invalidKey.setPubKey(TextUtil.toBase64(keyPair.getPublic().getEncoded()));
        invalidKey.setPrivKey(null);

        DSAServerConfig dsaServerConfig = new DSAServerConfig();
        dsaServerConfig.setPort(1234);
        dsaServerConfig.setLoggerFolder("/logger");
        dsaServerConfig.setKeyPair(invalidKey);
        rule.validate(dsaServerConfig);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateMessedKeys() {
        KeyPair keyPair = KeyDecoder.generateKeyPair();
        Base64KeyPair invalidKey = new Base64KeyPair();
        invalidKey.setPubKey(TextUtil.toBase64(keyPair.getPrivate().getEncoded()));
        invalidKey.setPrivKey(TextUtil.toBase64(keyPair.getPublic().getEncoded()));

        DSAServerConfig dsaServerConfig = new DSAServerConfig();
        dsaServerConfig.setPort(1234);
        dsaServerConfig.setLoggerFolder("/logger");
        dsaServerConfig.setKeyPair(invalidKey);
        rule.validate(dsaServerConfig);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidateDoublePublic() {
        KeyPair keyPair = KeyDecoder.generateKeyPair();
        Base64KeyPair invalidKey = new Base64KeyPair();
        invalidKey.setPubKey(TextUtil.toBase64(keyPair.getPublic().getEncoded()));
        invalidKey.setPrivKey(TextUtil.toBase64(keyPair.getPublic().getEncoded()));

        DSAServerConfig dsaServerConfig = new DSAServerConfig();
        dsaServerConfig.setPort(1234);
        dsaServerConfig.setLoggerFolder("/logger");
        dsaServerConfig.setKeyPair(invalidKey);
        rule.validate(dsaServerConfig);
    }

    @Test
    public void testValidateOk() {
        KeyPair keyPair = KeyDecoder.generateKeyPair();
        Base64KeyPair invalidKey = new Base64KeyPair();
        invalidKey.setPubKey(TextUtil.toBase64(keyPair.getPublic().getEncoded()));
        invalidKey.setPrivKey(TextUtil.toBase64(keyPair.getPrivate().getEncoded()));

        DSAServerConfig dsaServerConfig = new DSAServerConfig();
        dsaServerConfig.setPort(1234);
        dsaServerConfig.setLoggerFolder("/logger");
        dsaServerConfig.setKeyPair(invalidKey);
        rule.validate(dsaServerConfig);
    }
}
