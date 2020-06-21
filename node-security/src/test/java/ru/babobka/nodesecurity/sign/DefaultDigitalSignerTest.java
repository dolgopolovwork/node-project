package ru.babobka.nodesecurity.sign;

import org.junit.Test;
import ru.babobka.nodesecurity.data.SecureNodeRequest;
import ru.babobka.nodesecurity.data.SecureNodeResponse;
import ru.babobka.nodesecurity.keypair.KeyDecoder;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.data.Data;

import java.io.IOException;
import java.security.KeyPair;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DefaultDigitalSignerTest {
    private final KeyPair keyPair = KeyDecoder.generateKeyPair();
    private final DefaultDigitalSigner digitalSigner = new DefaultDigitalSigner(keyPair.getPrivate());
    private final SignatureValidator signatureValidator = new SignatureValidator();

    @Test
    public void testSignRequestTest() throws IOException {
        NodeRequest request = NodeRequest.regular(UUID.randomUUID(), "test_task", new Data());
        SecureNodeRequest secureNodeRequest = digitalSigner.sign(request);
        assertTrue(signatureValidator.isValid(secureNodeRequest, keyPair.getPublic()));
    }

    @Test
    public void testSignRequestInvalidPubKeyTest() throws IOException {
        NodeRequest request = NodeRequest.regular(UUID.randomUUID(), "test_task", new Data());
        SecureNodeRequest secureNodeRequest = digitalSigner.sign(request);
        assertFalse(signatureValidator.isValid(secureNodeRequest, KeyDecoder.generateKeyPair().getPublic()));
    }

    @Test
    public void testSignResponseTest() throws IOException {
        NodeResponse response = NodeResponse.dummy(UUID.randomUUID());
        SecureNodeResponse secureNodeResponse = digitalSigner.sign(response);
        assertTrue(signatureValidator.isValid(secureNodeResponse, keyPair.getPublic()));
    }

    @Test
    public void testSignResponseInvalidPrivKeyTest() throws IOException {
        NodeResponse response = NodeResponse.dummy(UUID.randomUUID());
        SecureNodeResponse secureNodeResponse = digitalSigner.sign(response);
        assertFalse(signatureValidator.isValid(secureNodeResponse, KeyDecoder.generateKeyPair().getPublic()));
    }
}
