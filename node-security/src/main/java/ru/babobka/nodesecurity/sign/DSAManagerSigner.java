package ru.babobka.nodesecurity.sign;

import lombok.NonNull;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import ru.babobka.nodesecurity.config.DSAManagerConfig;
import ru.babobka.nodesecurity.data.SecureNodeRequest;
import ru.babobka.nodesecurity.data.SecureNodeResponse;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.util.TextUtil;

import java.io.IOException;

public class DSAManagerSigner implements Signer {
    private final DSAManagerConfig config = Container.getInstance().get(DSAManagerConfig.class);

    @Override
    public SecureNodeRequest sign(NodeRequest request) throws IOException {
        String hashBase64 = TextUtil.toBase64(request.buildHash());
        return new SecureNodeRequest(request, getSignature(hashBase64));
    }

    @Override
    public SecureNodeResponse sign(@NonNull NodeResponse response) throws IOException {
        String hashBase64 = TextUtil.toBase64(response.buildHash());
        return new SecureNodeResponse(response, getSignature(hashBase64));
    }

    private byte[] getSignature(String hashBase64) throws IOException {
        return Request.Post("http://" + config.getHost() + ":" + config.getPort() + "/dsa/sign")
                .bodyString(hashBase64, ContentType.TEXT_PLAIN)
                .execute()
                .handleResponse(httpResponse -> {
                    StatusLine statusLine = httpResponse.getStatusLine();
                    HttpEntity entity = httpResponse.getEntity();
                    if (statusLine.getStatusCode() != 200) {
                        throw new IOException("Cannot sign the date. Response status from DSAM "
                                + statusLine.getStatusCode());
                    }
                    if (entity == null) {
                        throw new ClientProtocolException("Response contains no content");
                    }
                    return TextUtil.fromBase64(TextUtil.readStream(entity.getContent()));
                });
    }

}
