package ru.babobka.nodesecurity.keypair;

import lombok.NonNull;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import static ru.babobka.nodeutils.util.TextUtil.fromBase64;

public class KeyDecoder {

    private static final int KEY_SIZE = 2048;

    private KeyDecoder() {
    }

    private static KeyFactory createKeyFactory() {
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException ignored) {
            // Not going to happen
        }
        return keyFactory;
    }

    private static KeyPairGenerator createKeyPairGenerator() {
        KeyPairGenerator keyPairGenerator = null;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException ignored) {

        }
        return keyPairGenerator;
    }

    public static PrivateKey decodePrivateKey(@NonNull String base64) throws InvalidKeySpecException {
        return createKeyFactory()
                .generatePrivate(new PKCS8EncodedKeySpec(fromBase64(base64)));
    }

    public static PrivateKey decodePrivateKeyUnsafe(@NonNull String base64) {
        try {
            return decodePrivateKey(base64);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public static PublicKey decodePublicKey(@NonNull String base64)
            throws InvalidKeySpecException {
        byte[] decodedKey;
        try {
            decodedKey = fromBase64(base64);
        } catch (IllegalArgumentException ex) {
            throw new InvalidKeySpecException(ex);
        }
        return createKeyFactory().generatePublic(new X509EncodedKeySpec(decodedKey));
    }

    public static PublicKey decodePublicKeyUnsafe(@NonNull String base64) {
        try {
            return createKeyFactory().generatePublic(new X509EncodedKeySpec(fromBase64(base64)));
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public static KeyPair generateKeyPair() {
        KeyPairGenerator generator = createKeyPairGenerator();
        generator.initialize(KEY_SIZE, new SecureRandom());
        return generator.generateKeyPair();
    }
}
