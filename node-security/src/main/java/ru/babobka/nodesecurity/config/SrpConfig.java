package ru.babobka.nodesecurity.config;

import ru.babobka.nodeutils.math.Fp;
import ru.babobka.nodeutils.util.HashUtil;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Created by 123 on 29.04.2018.
 */
public class SrpConfig implements Serializable {

    private static final long serialVersionUID = -8006832397222674957L;
    private final Fp g;
    private final Fp k;
    private final int challengeBytes;

    public SrpConfig(Fp g, int challengeBytes) {
        if (g == null) {
            throw new IllegalArgumentException("g is null");
        } else if (challengeBytes < 1) {
            throw new IllegalArgumentException("there must be at least one byte to challenge");
        }
        this.g = g;
        BigInteger numK = new BigInteger(HashUtil.sha2(g.getMod().toByteArray(), g.getNumber().toByteArray()));
        this.k = new Fp(numK, g.getMod());
        this.challengeBytes = challengeBytes;
    }

    public Fp getG() {
        return g;
    }

    public Fp getK() {
        return k;
    }

    public int getChallengeBytes() {
        return challengeBytes;
    }
}
