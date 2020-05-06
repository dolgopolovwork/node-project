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

    public byte[] createSignature(@NonNull byte[] data, @NonNull PrivateKey privateKey) throws IOException {
        try {
            Signature privateSignature = Signature.getInstance(SIGNATURE_ALGORITHM);
            privateSignature.initSign(privateKey);
            privateSignature.update(data);
            return privateSignature.sign();
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            throw new IOException(e);
        }
    }

    public SecureNodeRequest sign(@NonNull NodeRequest request, @NonNull PrivateKey privateKey) throws IOException {
        byte[] signature = createSignature(buildHash(request), privateKey);
        return new SecureNodeRequest(request, signature);
    }

    public SecureNodeResponse sign(@NonNull NodeResponse response, @NonNull PrivateKey privateKey) throws IOException {
        byte[] signature = createSignature(buildHash(response), privateKey);
        return new SecureNodeResponse(response, signature);
    }

    public boolean isValid(@NonNull SecureNodeRequest request, @NonNull PublicKey publicKey) throws IOException {
        return isValid(buildHash(request), request.getSignature(), publicKey);
    }

    public boolean isValid(@NonNull SecureNodeResponse response, @NonNull PublicKey publicKey) throws IOException {
        return isValid(buildHash(response), response.getSignature(), publicKey);
    }

    public boolean isValid(@NonNull byte[] data, @NonNull byte[] signature, @NonNull PublicKey publicKey) throws IOException {
        try {
            Signature publicSignature = Signature.getInstance(SIGNATURE_ALGORITHM);
            publicSignature.initVerify(publicKey);
            publicSignature.update(data);
            return publicSignature.verify(signature);
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
