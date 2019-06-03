package ru.babobka.nodesecurity.keypair;

import org.junit.Test;
import ru.babobka.nodeutils.util.TextUtil;

import java.security.KeyPair;
import java.security.spec.InvalidKeySpecException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class KeyDecoderTest {

    @Test
    public void testDecodePubKey() throws InvalidKeySpecException {
        KeyPair keyPair = KeyDecoder.generateKeyPair();
        assertEquals(
                keyPair.getPublic(),
                KeyDecoder.decodePublicKey(TextUtil.toBase64(
                        keyPair.getPublic().getEncoded()
                )));
    }

    @Test(expected = InvalidKeySpecException.class)
    public void testDecodeInvalidPubKey() throws InvalidKeySpecException {
        KeyDecoder.decodePublicKey("ff");
    }

    @Test(expected = RuntimeException.class)
    public void testDecodeUnsafeInvalidPubKey() {
        KeyDecoder.decodePublicKeyUnsafe("ff");
    }

    @Test(expected = InvalidKeySpecException.class)
    public void testDecodeInvalidPrivKey() throws InvalidKeySpecException {
        KeyDecoder.decodePrivateKey("ff");
    }

    @Test(expected = RuntimeException.class)
    public void testDecodeUnsafeInvalidPrivKey() {
        KeyDecoder.decodePrivateKeyUnsafe("ff");
    }

    @Test
    public void testDecodeDifferentPubKey() throws InvalidKeySpecException {
        KeyPair keyPair1 = KeyDecoder.generateKeyPair();
        KeyPair keyPair2 = KeyDecoder.generateKeyPair();
        assertNotEquals(
                keyPair1.getPublic(),
                KeyDecoder.decodePublicKey(TextUtil.toBase64(
                        keyPair2.getPublic().getEncoded()
                )));
    }

    @Test
    public void testDecodePrivKey() throws InvalidKeySpecException {
        KeyPair keyPair = KeyDecoder.generateKeyPair();
        assertEquals(
                keyPair.getPrivate(),
                KeyDecoder.decodePrivateKey(TextUtil.toBase64(
                        keyPair.getPrivate().getEncoded()
                )));
    }

    @Test
    public void testDecodeDifferentPrivKey() throws InvalidKeySpecException {
        KeyPair keyPair1 = KeyDecoder.generateKeyPair();
        KeyPair keyPair2 = KeyDecoder.generateKeyPair();
        assertNotEquals(
                keyPair1.getPrivate(),
                KeyDecoder.decodePrivateKey(TextUtil.toBase64(
                        keyPair2.getPrivate().getEncoded()
                )));
    }
}
