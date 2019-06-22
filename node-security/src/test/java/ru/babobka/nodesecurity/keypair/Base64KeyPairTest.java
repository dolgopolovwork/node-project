package ru.babobka.nodesecurity.keypair;

import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class Base64KeyPairTest {

    @Test
    public void testPrivateKeyNotRevealed() {
        Base64KeyPair base64KeyPair = new Base64KeyPair();
        base64KeyPair.setPrivKey("abc");
        assertFalse(base64KeyPair.toString().contains(base64KeyPair.getPrivKey()));
    }
}
