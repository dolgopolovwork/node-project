package ru.babobka.nodeift;

import io.javalin.Javalin;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.babobka.nodeconfigs.dsa.DSAServerConfig;
import ru.babobka.nodedsa.server.NodeDSAApplicationContainer;
import ru.babobka.nodesecurity.config.DSAManagerConfig;
import ru.babobka.nodesecurity.data.SecureNodeRequest;
import ru.babobka.nodesecurity.data.SecureNodeResponse;
import ru.babobka.nodesecurity.keypair.Base64KeyPair;
import ru.babobka.nodesecurity.keypair.KeyDecoder;
import ru.babobka.nodesecurity.sign.DSAManagerSigner;
import ru.babobka.nodesecurity.sign.SignatureValidator;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.log.LoggerInit;
import ru.babobka.nodeutils.util.TextUtil;

import java.io.IOException;
import java.security.KeyPair;
import java.security.spec.InvalidKeySpecException;
import java.util.UUID;

import static org.junit.Assert.assertTrue;

public class NodeDSAManagerClientITCase {

    private static final DSAServerConfig config = new DSAServerConfig();
    private static final KeyPair keyPair = KeyDecoder.generateKeyPair();
    private static final SignatureValidator signatureValidator = new SignatureValidator();
    private static Javalin webServer;
    private static DSAManagerSigner dsaManagerSigner;

    @BeforeClass
    public static void setUp() {
        LoggerInit.initPersistentConsoleDebugLogger(TextUtil.getLogFolder(), NodeDSAManagerITCase.class.getSimpleName());
        config.setPort(1234);
        config.setLoggerFolder("/logger");
        Base64KeyPair base64KeyPair = new Base64KeyPair();
        base64KeyPair.setPrivKey(TextUtil.toBase64(keyPair.getPrivate().getEncoded()));
        base64KeyPair.setPubKey(TextUtil.toBase64(keyPair.getPublic().getEncoded()));
        config.setKeyPair(base64KeyPair);
        Container.getInstance().put(container -> {
            container.put(new NodeDSAApplicationContainer());
            container.put(KeyDecoder.decodePublicKey(config.getKeyPair().getPubKey()));
            container.put(KeyDecoder.decodePrivateKey(config.getKeyPair().getPrivKey()));
            DSAManagerConfig dsaManagerConfig = new DSAManagerConfig();
            dsaManagerConfig.setHost("localhost");
            dsaManagerConfig.setPort(config.getPort());
            container.put(dsaManagerConfig);
        });
        webServer = NodeDSAApplicationContainer.createWebServer();
        webServer.start(config.getPort());
        dsaManagerSigner = new DSAManagerSigner();
    }

    @AfterClass
    public static void tearDown() {
        webServer.stop();
        Container.getInstance().clear();
    }

    @Test
    public void testSignResponse() throws IOException, InvalidKeySpecException {
        NodeResponse dummyResponse = NodeResponse.dummy(UUID.randomUUID());
        SecureNodeResponse secureNodeResponse = dsaManagerSigner.sign(dummyResponse);
        assertTrue(signatureValidator
                .isValid(secureNodeResponse, KeyDecoder.decodePublicKey(
                        config.getKeyPair().getPubKey())));
    }

    @Test
    public void testSignRequest() throws IOException, InvalidKeySpecException {
        NodeRequest dummyRequest = NodeRequest.stop(UUID.randomUUID());
        SecureNodeRequest secureNodeRequest = dsaManagerSigner.sign(dummyRequest);
        assertTrue(signatureValidator
                .isValid(secureNodeRequest, KeyDecoder.decodePublicKey(
                        config.getKeyPair().getPubKey())));
    }

    @Test
    public void testSignResponseMassive() throws IOException, InvalidKeySpecException {
        for (int i = 0; i < 150; i++) {
            NodeResponse dummyResponse = NodeResponse.dummy(UUID.randomUUID());
            SecureNodeResponse secureNodeResponse = dsaManagerSigner.sign(dummyResponse);
            assertTrue(signatureValidator
                    .isValid(secureNodeResponse, KeyDecoder.decodePublicKey(
                            config.getKeyPair().getPubKey())));
        }
    }

    @Test
    public void testSignRequestMassive() throws IOException, InvalidKeySpecException {
        for (int i = 0; i < 150; i++) {
            NodeRequest dummyRequest = NodeRequest.stop(UUID.randomUUID());
            SecureNodeRequest secureNodeRequest = dsaManagerSigner.sign(dummyRequest);
            assertTrue(signatureValidator
                    .isValid(secureNodeRequest, KeyDecoder.decodePublicKey(
                            config.getKeyPair().getPubKey())));
        }
    }
}
