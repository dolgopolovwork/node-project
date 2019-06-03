package ru.babobka.nodesecurity.sign;

import lombok.NonNull;
import ru.babobka.nodesecurity.data.SecureNodeRequest;
import ru.babobka.nodesecurity.data.SecureNodeResponse;
import ru.babobka.nodeserials.NodeData;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeutils.util.HashUtil;

import java.io.IOException;
import java.security.*;

public class DigitalSigner {

    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";


    public SecureNodeRequest sign(@NonNull NodeRequest request, @NonNull PrivateKey privateKey) throws IOException {
        try {
            Signature privateSignature = Signature.getInstance(SIGNATURE_ALGORITHM);
            privateSignature.initSign(privateKey);
            privateSignature.update(buildHash(request));
            byte[] signature = privateSignature.sign();
            return new SecureNodeRequest(request, signature);

        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            throw new IOException(e);
        }
    }

    public SecureNodeResponse sign(@NonNull NodeResponse response, @NonNull PrivateKey privateKey) throws IOException {
        try {
            Signature privateSignature = Signature.getInstance(SIGNATURE_ALGORITHM);
            privateSignature.initSign(privateKey);
            privateSignature.update(buildHash(response));
            byte[] signature = privateSignature.sign();
            return new SecureNodeResponse(response, signature);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            throw new IOException(e);
        }

    }

    public boolean isValid(@NonNull SecureNodeRequest request, @NonNull PublicKey publicKey) throws IOException {
        try {
            Signature publicSignature = Signature.getInstance(SIGNATURE_ALGORITHM);
            publicSignature.initVerify(publicKey);
            publicSignature.update(buildHash(request));
            return publicSignature.verify(request.getSignature());
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            throw new IOException(e);
        }
    }

    public boolean isValid(@NonNull SecureNodeResponse response, @NonNull PublicKey publicKey) throws IOException {
        try {
            Signature publicSignature = Signature.getInstance(SIGNATURE_ALGORITHM);
            publicSignature.initVerify(publicKey);
            publicSignature.update(buildHash(response));
            return publicSignature.verify(response.getSignature());
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            throw new IOException(e);
        }
    }

    private byte[] buildHash(NodeData nodeData) {
        byte[] metaHash = HashUtil.sha2(
                HashUtil.safeHashCode(nodeData.getId()),
                HashUtil.safeHashCode(nodeData.getTaskId()),
                HashUtil.safeHashCode(nodeData.getTaskName()),
                (int) nodeData.getTimeStamp());
        byte[] dataHash = HashUtil.sha2(nodeData.getData().getIterator());
        byte[] mainHash = HashUtil.sha2(dataHash, metaHash);
        if (nodeData instanceof NodeResponse) {
            NodeResponse nodeResponse = (NodeResponse) nodeData;
            return buildHash(nodeResponse, mainHash);
        }
        return mainHash;
    }

    private byte[] buildHash(NodeResponse nodeResponse, byte[] mainHash) {
        byte[] smallHash = HashUtil.sha2(
                nodeResponse.getStatus().ordinal(),
                HashUtil.safeHashCode(nodeResponse.getMessage()),
                (int) nodeResponse.getTimeTakes());
        return HashUtil.sha2(mainHash, smallHash);
    }
}
