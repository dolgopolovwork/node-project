package ru.babobka.nodeift;

import ru.babobka.nodebusiness.service.DebugBase64KeyPair;
import ru.babobka.nodesecurity.keypair.KeyDecoder;

import java.security.PrivateKey;

/**
 * Created by 123 on 18.05.2018.
 */
public interface TestCredentials {
    String USER_NAME = "test_user";
    PrivateKey PRIV_KEY = KeyDecoder.decodePrivateKeyUnsafe(DebugBase64KeyPair.DEBUG_PRIV_KEY);
}
