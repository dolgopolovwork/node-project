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

public class DigitalSignerTest {

    private final DigitalSigner digitalSigner = new DigitalSigner();

    @Test
    public void testSignRequestTest() throws IOException {
        KeyPair keyPair = KeyDecoder.generateKeyPair();
        NodeRequest request = NodeRequest.regular(UUID.randomUUID(), "test_task", new Data());
        SecureNodeRequest secureNodeRequest = digitalSigner.sign(request, keyPair.getPrivate());
        assertTrue(digitalSigner.isValid(secureNodeRequest, keyPair.getPublic()));
    }

    @Test
    public void testSignRequestInvalidPubKeyTest() throws IOException {
        KeyPair keyPair = KeyDecoder.generateKeyPair();
        NodeRequest request = NodeRequest.regular(UUID.randomUUID(), "test_task", new Data());
        SecureNodeRequest secureNodeRequest = digitalSigner.sign(request, keyPair.getPrivate());
        assertFalse(digitalSigner.isValid(secureNodeRequest, KeyDecoder.generateKeyPair().getPublic()));
    }

    @Test
    public void testSignResponseTest() throws IOException {
        KeyPair keyPair = KeyDecoder.generateKeyPair();
        NodeResponse response = NodeResponse.dummy(UUID.randomUUID());
        SecureNodeResponse secureNodeResponse = digitalSigner.sign(response, keyPair.getPrivate());
        assertTrue(digitalSigner.isValid(secureNodeResponse, keyPair.getPublic()));
    }

    @Test
    public void testSignResponseInvalidPrivKeyTest() throws IOException {
        KeyPair keyPair = KeyDecoder.generateKeyPair();
        NodeResponse response = NodeResponse.dummy(UUID.randomUUID());
        SecureNodeResponse secureNodeResponse = digitalSigner.sign(response, keyPair.getPrivate());
        assertFalse(digitalSigner.isValid(secureNodeResponse, KeyDecoder.generateKeyPair().getPublic()));
    }
}
