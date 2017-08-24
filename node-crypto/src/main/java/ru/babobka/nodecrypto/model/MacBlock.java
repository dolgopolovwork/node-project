package ru.babobka.nodecrypto.model;

import java.io.Serializable;

/**
 * Created by 123 on 17.07.2017.
 */
public class MacBlock implements Serializable {

    private static final long serialVersionUID = -299149584072413221L;
    private final byte[] iv;
    private final byte[] mac;

    public MacBlock(byte[] iv, byte[] mac) {
        if (iv == null) {
            throw new IllegalArgumentException("iv is null");
        } else if (mac == null) {
            throw new IllegalArgumentException("mac is null");
        }
        this.iv = iv.clone();
        this.mac = mac.clone();
    }

    public byte[] getIv() {
        return iv.clone();
    }

    public byte[] getMac() {
        return mac.clone();
    }

}
