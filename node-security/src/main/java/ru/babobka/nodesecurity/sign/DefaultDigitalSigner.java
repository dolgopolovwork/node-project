package ru.babobka.nodesecurity.sign;

import lombok.NonNull;
import ru.babobka.nodesecurity.data.SecureNodeRequest;
import ru.babobka.nodesecurity.data.SecureNodeResponse;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;

import java.io.IOException;
import java.security.PrivateKey;

public class DefaultDigitalSigner implements Signer {

    private final PrivateKey privateKey;

    public DefaultDigitalSigner(@NonNull PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    @Override
    public SecureNodeRequest sign(NodeRequest request) throws IOException {
        byte[] signature = SignUtil.createSignature(request.buildHash(), privateKey);
        return new SecureNodeRequest(request, signature);
    }

    @Override
    public SecureNodeResponse sign(@NonNull NodeResponse response) throws IOException {
        byte[] signature = SignUtil.createSignature(response.buildHash(), privateKey);
        return new SecureNodeResponse(response, signature);
    }
}
