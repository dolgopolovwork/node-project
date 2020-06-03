package ru.babobka.nodesecurity.sign;

import lombok.NonNull;
import ru.babobka.nodesecurity.data.SecureNodeRequest;
import ru.babobka.nodesecurity.data.SecureNodeResponse;
import ru.babobka.nodeutils.util.TextUtil;

import java.io.IOException;
import java.security.*;

import static ru.babobka.nodesecurity.sign.SignUtil.SIGNATURE_ALGORITHM;

public class SignatureValidator {

    public boolean isValid(@NonNull SecureNodeRequest request, @NonNull PublicKey publicKey) throws IOException {
        return isValid(request.buildHash(), request.getSignature(), publicKey);
    }

    public boolean isValid(@NonNull SecureNodeResponse response, @NonNull PublicKey publicKey) throws IOException {
        return isValid(response.buildHash(), response.getSignature(), publicKey);
    }

    public static boolean isValid(@NonNull byte[] data, @NonNull byte[] signature, @NonNull PublicKey publicKey) throws IOException {
        try {
            Signature publicSignature = Signature.getInstance(SIGNATURE_ALGORITHM);
            publicSignature.initVerify(publicKey);
            publicSignature.update(data);
            return publicSignature.verify(signature);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            throw new IOException(e);
        }
    }

}
