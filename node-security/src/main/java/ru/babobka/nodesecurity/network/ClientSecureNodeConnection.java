package ru.babobka.nodesecurity.network;

import ru.babobka.nodesecurity.sign.Signer;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeutils.network.NodeConnection;
import ru.babobka.nodeutils.time.ServerTime;

import java.io.IOException;
import java.security.PublicKey;

/**
 * Created by 123 on 07.06.2018.
 */
public class ClientSecureNodeConnection extends SecureNodeConnection {
    private final ServerTime serverTime;

    public ClientSecureNodeConnection(
            Signer signer,
            ServerTime serverTime,
            NodeConnection nodeConnection,
            PublicKey publicKey) {
        super(signer, nodeConnection, publicKey);
        this.serverTime = serverTime;
    }

    @Override
    protected void send(NodeResponse response) throws IOException {
        response.setTimeStamp(serverTime.getTime());
        super.send(response);
    }

}
