package ru.babobka.nodeconfigs.slave.validation.rule;

import ru.babobka.nodeconfigs.slave.SlaveServerConfig;
import ru.babobka.nodesecurity.keypair.KeyDecoder;
import ru.babobka.nodeutils.validation.ValidationRule;

import java.security.spec.InvalidKeySpecException;

/**
 * Created by 123 on 26.05.2018.
 */
public class ServerPublicKeyValidationRule implements ValidationRule<SlaveServerConfig> {
    @Override
    public void validate(SlaveServerConfig slaveServerConfig) {
        if (slaveServerConfig.getMasterServerBase64PublicKey() == null) {
            throw new IllegalArgumentException("server public key was not set");
        }
        try {
            KeyDecoder.decodePublicKey(slaveServerConfig.getMasterServerBase64PublicKey());
        } catch (InvalidKeySpecException e) {
            throw new IllegalStateException("Cannot decode server public key", e);
        }
    }
}
