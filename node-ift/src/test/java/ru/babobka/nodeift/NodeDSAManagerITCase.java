package ru.babobka.nodeift;

import io.javalin.Javalin;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.babobka.nodeconfigs.dsa.DSAServerConfig;
import ru.babobka.nodedsa.server.NodeDSAApplicationContainer;
import ru.babobka.nodesecurity.keypair.Base64KeyPair;
import ru.babobka.nodesecurity.keypair.KeyDecoder;
import ru.babobka.nodesecurity.sign.DigitalSigner;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.log.LoggerInit;
import ru.babobka.nodeutils.util.TextUtil;

import java.io.IOException;
import java.security.KeyPair;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NodeDSAManagerITCase {

    private static final DigitalSigner digitalSigner = new DigitalSigner();
    private static final DSAServerConfig config = new DSAServerConfig();
    private static final KeyPair keyPair = KeyDecoder.generateKeyPair();
    private static Javalin webServer;

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
        });
        webServer = NodeDSAApplicationContainer.createWebServer();
        webServer.start(config.getPort());
    }

    @AfterClass
    public static void tearDown() {
        webServer.stop();
        Container.getInstance().clear();
    }

    @Test
    public void testGetPublicKey() throws IOException {
        String pubKeyBase64Response = Request.Get("http://127.0.0.1:" + config.getPort() + "/dsa/pubKey")
                .execute().returnContent().toString();
        assertEquals(config.getKeyPair().getPubKey(), pubKeyBase64Response);
    }

    @Test
    public void testSign() throws IOException {
        String dataToSignBase64 = TextUtil.toBase64("abc".getBytes(TextUtil.CHARSET));
        String signatureBase64 = Request.Post("http://127.0.0.1:" + config.getPort() + "/dsa/sign")
                .bodyString(dataToSignBase64, ContentType.TEXT_PLAIN)
                .execute().returnContent().toString();
        assertTrue(digitalSigner.isValid(TextUtil.fromBase64(dataToSignBase64), TextUtil.fromBase64(signatureBase64), keyPair.getPublic()));
    }

    @Test
    public void testSignMultiple() throws IOException {
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            int length = random.nextInt(1024) + 1;
            byte[] data = new byte[length];
            random.nextBytes(data);
            String dataToSignBase64 = TextUtil.toBase64(data);
            String signatureBase64 = Request.Post("http://127.0.0.1:" + config.getPort() + "/dsa/sign")
                    .bodyString(dataToSignBase64, ContentType.TEXT_PLAIN)
                    .execute().returnContent().toString();
            assertTrue(digitalSigner.isValid(data, TextUtil.fromBase64(signatureBase64), keyPair.getPublic()));
        }
    }
}
