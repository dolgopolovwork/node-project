package ru.babobka.nodesecurity.keypair;

import lombok.NonNull;

import java.io.Serializable;

public class Base64KeyPair implements Serializable {

    private static final long serialVersionUID = -1404319784836367181L;
    private String pubKey;
    private String privKey;

    public String getPubKey() {
        return pubKey;
    }

    public void setPubKey(@NonNull String pubKey) {
        this.pubKey = pubKey;
    }

    public String getPrivKey() {
        return privKey;
    }

    public void setPrivKey(@NonNull String privKey) {
        this.privKey = privKey;
    }

}
