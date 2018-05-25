package ru.babobka.nodesecurity.rsa;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Created by 123 on 19.05.2018.
 */
public class RSAPublicKey implements Serializable {
    private static final long serialVersionUID = 4644501188529658582L;
    private final BigInteger e;
    private final BigInteger n;

    public RSAPublicKey(BigInteger e, BigInteger n) {
        if (e == null) {
            throw new IllegalArgumentException("e was not set");
        } else if (n == null) {
            throw new IllegalArgumentException("n was not set");
        }
        this.e = e;
        this.n = n;
    }

    public BigInteger getE() {
        return e;
    }

    public BigInteger getN() {
        return n;
    }
}
