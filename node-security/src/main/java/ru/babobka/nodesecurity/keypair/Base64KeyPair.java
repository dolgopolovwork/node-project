package ru.babobka.nodesecurity.keypair;

import java.io.Serializable;
import java.util.Objects;

public class Base64KeyPair implements Serializable {

    private static final long serialVersionUID = -1404319784836367181L;
    private String pubKey;
    private String privKey;

    public String getPubKey() {
        return pubKey;
    }

    public void setPubKey(String pubKey) {
        this.pubKey = pubKey;
    }

    public String getPrivKey() {
        return privKey;
    }

    public void setPrivKey(String privKey) {
        this.privKey = privKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Base64KeyPair that = (Base64KeyPair) o;
        return Objects.equals(pubKey, that.pubKey) &&
                Objects.equals(privKey, that.privKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pubKey, privKey);
    }

    @Override
    public String toString() {
        return "Base64KeyPair{" +
                "pubKey='" + pubKey + '\'' +
                ", privKey='***'" +
                '}';
    }
}
