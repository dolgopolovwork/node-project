package ru.babobka.nodesecurity.service;

import ru.babobka.nodesecurity.rsa.RSAPrivateKey;
import ru.babobka.nodesecurity.rsa.RSAPublicKey;

import java.math.BigInteger;

/**
 * Created by 123 on 20.05.2018.
 */
public class RSAService {
    public BigInteger encrypt(BigInteger m, RSAPublicKey publicKey) {
        if (m == null) {
            throw new IllegalArgumentException("m is null");
        } else if (publicKey == null) {
            throw new IllegalArgumentException("publicKey is null");
        }
        return m.modPow(publicKey.getE(), publicKey.getN());
    }

    public BigInteger decrypt(BigInteger c, RSAPrivateKey privateKey) {
        if (c == null) {
            throw new IllegalArgumentException("c is null");
        } else if (privateKey == null) {
            throw new IllegalArgumentException("privateKey is null");
        }
        return c.modPow(privateKey.getD(), privateKey.getN());
    }
}
