package ru.babobka.nodecrypto.model;

import java.math.BigInteger;

/**
 * Created by 123 on 05.07.2017.
 */
public final class CryptoServer {

    private CryptoParams cryptoParams;

    private BigInteger verifier;

    private byte[] salt;

    private BigInteger b;

    private BigInteger expA;

    private byte[] clientSalt;

    private String clientIdentity;


    public CryptoParams getCryptoParams() {
        return cryptoParams;
    }

    public void setCryptoParams(CryptoParams cryptoParams) {
        this.cryptoParams = cryptoParams;
    }

    public BigInteger getVerifier() {
        return verifier;
    }

    public void setVerifier(BigInteger verifier) {
        this.verifier = verifier;
    }

    public byte[] getSalt() {
        return salt.clone();
    }

    public void setSalt(byte[] salt) {
        this.salt = salt.clone();
    }

    public BigInteger getB() {
        return b;
    }

    public void setB(BigInteger b) {
        this.b = b;
    }

    public BigInteger getExpA() {
        return expA;
    }

    public void setExpA(BigInteger expA) {
        this.expA = expA;
    }

    public byte[] getClientSalt() {
        return clientSalt.clone();
    }

    public void setClientSalt(byte[] clientSalt) {
        this.clientSalt = clientSalt.clone();
    }

    public String getClientIdentity() {
        return clientIdentity;
    }

    public void setClientIdentity(String clientIdentity) {
        this.clientIdentity = clientIdentity;
    }
}
