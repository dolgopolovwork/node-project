package ru.babobka.nodebusiness.debug;

import ru.babobka.nodesecurity.keypair.KeyDecoder;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Created by 123 on 18.05.2018.
 */
public interface DebugCredentials {
    String USER_NAME = "test_user";
    PrivateKey PRIV_KEY = KeyDecoder.decodePrivateKeyUnsafe(DebugBase64KeyPair.DEBUG_PRIV_KEY);
    PublicKey PUB_KEY = KeyDecoder.decodePublicKeyUnsafe(DebugBase64KeyPair.DEBUG_PUB_KEY);
}
