package ru.babobka.nodesecurity.checker;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodesecurity.data.SecureNodeRequest;
import ru.babobka.nodesecurity.data.SecureNodeResponse;
import ru.babobka.nodesecurity.keypair.KeyDecoder;
import ru.babobka.nodesecurity.sign.DigitalSigner;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.data.Data;
import ru.babobka.nodeutils.container.Container;

import java.io.IOException;
import java.security.KeyPair;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SecureDataCheckerTest {
    private DigitalSigner digitalSigner;
    private SecureDataChecker secureDataChecker;
    private KeyPair keyPair;

    @Before
    public void setUp() {
        keyPair = KeyDecoder.generateKeyPair();
        digitalSigner = mock(DigitalSigner.class);
        Container.getInstance().put(digitalSigner);
        secureDataChecker = new SecureDataChecker();
    }

    @After
    public void tearDown() {
        Container.getInstance().clear();
    }

    @Test
    public void testIsSecureBadObject() throws IOException {
        assertFalse(secureDataChecker.isSecure(new Object(), keyPair.getPublic()));
    }

    @Test
    public void testIsSecureHeartBeatRequest() throws IOException {
        assertTrue(secureDataChecker.isSecure(NodeRequest.heartBeat(), keyPair.getPublic()));
    }

    @Test
    public void testIsSecureHeartBeatResponse() throws IOException {
        assertTrue(secureDataChecker.isSecure(NodeResponse.heartBeat(), keyPair.getPublic()));
    }

    @Test
    public void testIsSecurePlainResponse() throws IOException {
        assertFalse(secureDataChecker.isSecure(NodeResponse.dummy(UUID.randomUUID()), keyPair.getPublic()));
    }

    @Test
    public void testIsSecurePlainRequest() throws IOException {
        assertFalse(secureDataChecker.isSecure(
                NodeRequest.regular(UUID.randomUUID(), "test", new Data()),
                keyPair.getPublic()));
    }

    @Test
    public void testIsSecureRequest() throws IOException {
        SecureNodeRequest secureNodeRequest = mock(SecureNodeRequest.class);
        when(digitalSigner.isValid(eq(secureNodeRequest), eq(keyPair.getPublic()))).thenReturn(true);
        assertTrue(secureDataChecker.isSecure(
                secureNodeRequest,
                keyPair.getPublic()));
    }

    @Test
    public void testIsNotSecureRequest() throws IOException {
        SecureNodeRequest secureNodeRequest = mock(SecureNodeRequest.class);
        when(digitalSigner.isValid(eq(secureNodeRequest), eq(keyPair.getPublic()))).thenReturn(false);
        assertFalse(secureDataChecker.isSecure(
                secureNodeRequest,
                keyPair.getPublic()));
    }

    @Test
    public void testIsSecureResponse() throws IOException {
        SecureNodeResponse secureNodeResponse = mock(SecureNodeResponse.class);
        when(digitalSigner.isValid(eq(secureNodeResponse), eq(keyPair.getPublic()))).thenReturn(true);
        assertTrue(secureDataChecker.isSecure(
                secureNodeResponse,
                keyPair.getPublic()));
    }

    @Test
    public void testIsNotSecureResponse() throws IOException {
        SecureNodeResponse secureNodeResponse = mock(SecureNodeResponse.class);
        when(digitalSigner.isValid(eq(secureNodeResponse), eq(keyPair.getPublic()))).thenReturn(false);
        assertFalse(secureDataChecker.isSecure(
                secureNodeResponse,
                keyPair.getPublic()));
    }
}
