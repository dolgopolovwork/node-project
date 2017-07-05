package ru.babobka.nodecrypto;

import java.math.BigInteger;

/**
 * Created by 123 on 05.07.2017.
 */
public final class CryptoServer {

    private final CryptoParams cryptoParams;

    private final BigInteger verifier;

    private final BigInteger salt;

    private final BigInteger b;

    private final BigInteger expB;

    private final byte[] clientSalt;

    private final byte[] key;

    public CryptoServer(CryptoParams cryptoParams, BigInteger verifier, BigInteger salt, BigInteger b, BigInteger expB, byte[] clientSalt, byte[] key) {
        this.cryptoParams = cryptoParams;
        this.verifier = verifier;
        this.salt = salt;
        this.b = b;
        this.expB = expB;
        this.clientSalt = clientSalt;
        this.key = key;
    }

}
