package ru.babobka.nodesecurity.network;

import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeutils.network.NodeConnection;
import ru.babobka.nodeutils.time.ServerTime;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Created by 123 on 07.06.2018.
 */
public class ClientSecureNodeConnection extends SecureNodeConnection {
    private final ServerTime serverTime;

    public ClientSecureNodeConnection(
            ServerTime serverTime,
            NodeConnection nodeConnection,
            PrivateKey privateKey,
            PublicKey publicKey) {
        super(nodeConnection, privateKey, publicKey);
        this.serverTime = serverTime;
    }

    @Override
    protected void send(NodeResponse response) throws IOException {
        response.setTimeStamp(serverTime.getTime());
        super.send(response);
    }

}
