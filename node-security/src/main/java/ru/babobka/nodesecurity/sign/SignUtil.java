package ru.babobka.nodesecurity.sign;

import lombok.NonNull;

import java.io.IOException;
import java.security.*;

public class SignUtil {

    public static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

    public static byte[] createSignature(@NonNull byte[] data, @NonNull PrivateKey privateKey) throws IOException {
        try {
            Signature privateSignature = Signature.getInstance(SIGNATURE_ALGORITHM);
            privateSignature.initSign(privateKey);
            privateSignature.update(data);
            return privateSignature.sign();
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            throw new IOException(e);
        }
    }
}
