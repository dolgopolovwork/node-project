package ru.babobka.nodesecurity.rsa;

import java.io.Serializable;

/**
 * Created by 123 on 20.05.2018.
 */
public class RSAConfig implements Serializable {
    private static final long serialVersionUID = 5891053949542777727L;
    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;

    public RSAConfig(RSAPrivateKey privateKey, RSAPublicKey publicKey) {
        if (privateKey == null) {
            throw new IllegalArgumentException("privateKey is null");
        } else if (publicKey == null) {
            throw new IllegalArgumentException("publicKey is null");
        } else if (!privateKey.getN().equals(publicKey.getN())) {
            throw new IllegalArgumentException("publicKey and private keys must have the same modulus");
        }
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public RSAPrivateKey getPrivateKey() {
        return privateKey;
    }

    public RSAPublicKey getPublicKey() {
        return publicKey;
    }
}
