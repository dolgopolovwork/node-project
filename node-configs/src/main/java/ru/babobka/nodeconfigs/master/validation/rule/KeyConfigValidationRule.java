package ru.babobka.nodeconfigs.master.validation.rule;


import ru.babobka.nodeconfigs.master.MasterServerConfig;
import ru.babobka.nodesecurity.keypair.Base64KeyPair;
import ru.babobka.nodesecurity.keypair.KeyDecoder;
import ru.babobka.nodeutils.util.TextUtil;
import ru.babobka.nodeutils.validation.ValidationRule;

import java.security.spec.InvalidKeySpecException;

/**
 * Created by 123 on 04.05.2018.
 */
public class KeyConfigValidationRule implements ValidationRule<MasterServerConfig> {
    @Override
    public void validate(MasterServerConfig config) {
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
