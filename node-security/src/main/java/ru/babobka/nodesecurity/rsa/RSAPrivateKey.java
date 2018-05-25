package ru.babobka.nodesecurity.rsa;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Created by 123 on 20.05.2018.
 */
public class RSAPrivateKey implements Serializable {
    private static final long serialVersionUID = -3863102388760310808L;
    private final BigInteger d;
    private final BigInteger n;

    public RSAPrivateKey(BigInteger d, BigInteger n) {
        if (d == null) {
            throw new IllegalArgumentException("d was not set");
        } else if (n == null) {
            throw new IllegalArgumentException("n was not set");
        }
        this.d = d;
        this.n = n;
    }

    public BigInteger getD() {
        return d;
    }

    public BigInteger getN() {
        return n;
    }
}
