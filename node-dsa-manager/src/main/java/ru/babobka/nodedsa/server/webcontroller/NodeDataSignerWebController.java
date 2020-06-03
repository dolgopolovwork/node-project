package ru.babobka.nodedsa.server.webcontroller;

import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.*;
import org.apache.log4j.Logger;
import ru.babobka.nodesecurity.sign.SignUtil;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.util.TextUtil;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.UUID;

public class NodeDataSignerWebController {
    private PublicKey publicKey = Container.getInstance().get(PublicKey.class);
    private PrivateKey privateKey = Container.getInstance().get(PrivateKey.class);
    private static final Logger logger = Logger.getLogger(NodeDataSignerWebController.class);

    @OpenApi(
            method = HttpMethod.POST,
            path = "/dsa/sign",
            summary = "Signs a given base64 encoded data array",
            operationId = "sign",
            tags = {"DSA"},
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = String.class)}),
            responses = {@OpenApiResponse(status = "200", content = {@OpenApiContent(from = String.class)})}
    )
    public void signData(Context context) {
        byte[] contentToSign = TextUtil.fromBase64(context.body());
        UUID operationId = UUID.randomUUID();
        try {
            logger.info("Operation id:" + operationId + ". Sign data  '" + context.body() + "'");
            byte[] signature = SignUtil.createSignature(contentToSign, privateKey);
            logger.info("Operation id:" + operationId + ". Signed.");
            context.result(TextUtil.toBase64(signature));
        } catch (IOException e) {
            logger.error("Operation id:" + operationId + ". Cannot sign", e);
            context.status(500);
            context.result("Signature failure");
        }
    }

    @OpenApi(
            method = HttpMethod.GET,
            path = "/dsa/pubKey",
            summary = "Returns a public key which corresponding private key is used to sign data",
            operationId = "getPubKey",
            tags = {"DSA"},
            responses = {@OpenApiResponse(status = "200", content = {@OpenApiContent(from = String.class)})}
    )
    public void getPublicKey(Context context) {
        String base64PubKey = TextUtil.toBase64(publicKey.getEncoded());
        logger.info("Request signature");
        context.result(base64PubKey);
    }
}
