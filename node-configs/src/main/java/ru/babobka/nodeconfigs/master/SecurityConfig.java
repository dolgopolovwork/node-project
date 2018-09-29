package ru.babobka.nodeconfigs.master;

import ru.babobka.nodesecurity.rsa.RSAConfig;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Created by 123 on 13.05.2018.
 */
public class SecurityConfig implements Serializable {
    private static final long serialVersionUID = -1315130732356373527L;
    private BigInteger bigSafePrime;
    private RSAConfig rsaConfig;
    private int challengeBytes;

    public BigInteger getBigSafePrime() {
        return bigSafePrime;
    }

    public void setBigSafePrime(BigInteger bigSafePrime) {
        this.bigSafePrime = bigSafePrime;
    }

    public int getChallengeBytes() {
        return challengeBytes;
    }

    public void setChallengeBytes(int challengeBytes) {
        this.challengeBytes = challengeBytes;
    }

    public RSAConfig getRsaConfig() {
        return rsaConfig;
    }

    public void setRsaConfig(RSAConfig rsaConfig) {
        this.rsaConfig = rsaConfig;
    }
}
