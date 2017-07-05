package ru.babobka.nodecrypto.crypto;

/**
 * Created by 123 on 03.07.2017.
 */
public interface CryptoService {

    byte[] encrypt(byte[] message);

    byte[] decrypt(byte[] cipher);

}
