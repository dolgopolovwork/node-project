package ru.babobka.nodecrypto.hash;

/**
 * Created by 123 on 03.07.2017.
 */
public interface HashService {

    byte[] hash(byte[] bytes);

    byte[] hash(byte[] bytes, byte[] salt);
}
