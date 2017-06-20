package ru.babobka.nodeserials.crypto;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Created by dolgopolov.a on 29.10.15.
 */
public class PrivateKey implements Serializable {

    private static final long serialVersionUID = 1L;

    private final BigInteger n;

    private final BigInteger d;

    public PrivateKey(BigInteger n, BigInteger d) {
        this.n = n;
        this.d = d;
    }

    public BigInteger getN() {
        return n;
    }

    public BigInteger getD() {
        return d;
    }

}
