package ru.babobka.nodeconfigs.slave.validation.rule;

import ru.babobka.nodeconfigs.slave.SlaveServerConfig;
import ru.babobka.nodesecurity.keypair.Base64KeyPair;
import ru.babobka.nodesecurity.keypair.KeyDecoder;
import ru.babobka.nodeutils.util.TextUtil;
import ru.babobka.nodeutils.validation.ValidationRule;

import java.security.spec.InvalidKeySpecException;

public class KeyConfigValidationRule implements ValidationRule<SlaveServerConfig> {
    @Override
    public void validate(SlaveServerConfig config) {
        Base64KeyPair keyPair = config.getKeyPair();
        if (keyPair == null) {
            throw new IllegalArgumentException("keyPair was not set");
        } else if (TextUtil.isEmpty(keyPair.getPrivKey())) {
            throw new IllegalArgumentException("privKey was not set");
        } else if (TextUtil.isEmpty(keyPair.getPubKey())) {
            throw new IllegalArgumentException("pubKey was not set");
        }
        try {
            KeyDecoder.decodePublicKey(keyPair.getPubKey());
        } catch (InvalidKeySpecException e) {
            throw new IllegalArgumentException("Cannot decode public key", e);
        }

        try {
            KeyDecoder.decodePrivateKey(keyPair.getPrivKey());
        } catch (InvalidKeySpecException e) {
            throw new IllegalArgumentException("Cannot decode private key", e);
        }
    }
}
