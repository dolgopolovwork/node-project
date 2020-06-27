package ru.babobka.nodeift;

import io.javalin.Javalin;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.babobka.nodeconfigs.dsa.DSAServerConfig;
import ru.babobka.nodedsa.server.NodeDSAApplicationContainer;
import ru.babobka.nodesecurity.config.DSAManagerConfig;
import ru.babobka.nodesecurity.keypair.Base64KeyPair;
import ru.babobka.nodesecurity.keypair.KeyDecoder;
import ru.babobka.nodesecurity.sign.DSAManagerSigner;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.log.LoggerInit;
import ru.babobka.nodeutils.util.TextUtil;

import java.io.IOException;
import java.security.KeyPair;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class NodeDSAManagerClientResiliencyITCase {

    private static final DSAServerConfig config = new DSAServerConfig();
    private static final KeyPair keyPair = KeyDecoder.generateKeyPair();
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
            dsaManagerConfig.setHost("~wrong.host.no.way.it.exists~"); // wrong host
            dsaManagerConfig.setPort(-666); // wrong port
            container.put(dsaManagerConfig);
        });
        webServer = NodeDSAApplicationContainer.createWebServer();
        webServer.start(config.getPort());
        dsaManagerSigner = spy(new DSAManagerSigner());
    }

    @AfterClass
    public static void tearDown() {
        webServer.stop();
        Container.getInstance().clear();
    }

    @Test
    public void testSignResponseMassiveFailure() throws IOException, InterruptedException {
        for (int i = 0; i < 110; i++) {
            NodeResponse dummyResponse = NodeResponse.dummy(UUID.randomUUID());
            try {
                dsaManagerSigner.sign(dummyResponse);
            } catch (Exception ignored) {
                // it's ok to fail here
            }
        }
        verify(dsaManagerSigner, times(100)).getSignature(anyString());
    }

    @Test
    public void testSignRequestMassiveFailure() throws IOException, InterruptedException {
        for (int i = 0; i < 110; i++) {
            NodeRequest dummyRequest = NodeRequest.stop(UUID.randomUUID());
            try {
                dsaManagerSigner.sign(dummyRequest);
            } catch (Exception ignored) {
                // it's ok to fail here
            }
        }
        verify(dsaManagerSigner, times(100)).getSignature(anyString());
    }

}
