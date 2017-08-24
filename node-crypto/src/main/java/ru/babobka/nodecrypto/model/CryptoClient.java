package ru.babobka.nodecrypto.model;

import java.math.BigInteger;

/**
 * Created by 123 on 03.07.2017.
 */
public final class CryptoClient {


    private CryptoParams cryptoParams;

    private byte[] secret;

    private String identity;

    private byte[] salt;

    private BigInteger a;

    private BigInteger expB;

    private byte[] serverSalt;


    public CryptoParams getCryptoParams() {
        return cryptoParams;
    }

    public void setCryptoParams(CryptoParams cryptoParams) {
        this.cryptoParams = cryptoParams;
    }

    public byte[] getSecret() {
        return secret.clone();
    }

    public void setSecret(byte[] secret) {
        this.secret = secret.clone();
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public byte[] getSalt() {
        return salt.clone();
    }

    public void setSalt(byte[] salt) {
        this.salt = salt.clone();
    }

    public BigInteger getA() {
        return a;
    }

    public void setA(BigInteger a) {
        this.a = a;
    }

    public BigInteger getExpB() {
        return expB;
    }

    public void setExpB(BigInteger expB) {
        this.expB = expB;
    }

    public byte[] getServerSalt() {
        return serverSalt.clone();
    }

    public void setServerSalt(byte[] serverSalt) {
        this.serverSalt = serverSalt.clone();
    }
}
