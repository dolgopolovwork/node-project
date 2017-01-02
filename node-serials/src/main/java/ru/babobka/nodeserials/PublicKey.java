package ru.babobka.nodeserials;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Created by dolgopolov.a on 29.10.15.
 */
public class PublicKey  implements Serializable {

    private static final long serialVersionUID = 1L;

    private final BigInteger n;

    private final BigInteger e;

    public PublicKey(BigInteger n, BigInteger e) {
        this.n = n;
        this.e = e;
    }

    public BigInteger getN() {
        return n;
    }

    public BigInteger getE() {
        return e;
    }
}
