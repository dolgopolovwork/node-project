package ru.babobka.nodesecurity.network;

import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeutils.network.NodeConnection;
import ru.babobka.nodeutils.time.ServerTime;

import java.io.IOException;

/**
 * Created by 123 on 07.06.2018.
 */
public class ClientSecureNodeConnection extends SecureNodeConnection {
    private final ServerTime serverTime;

    public ClientSecureNodeConnection(ServerTime serverTime, NodeConnection nodeConnection, byte[] secretKey) {
        super(nodeConnection, secretKey);
        if (serverTime == null) {
            throw new IllegalArgumentException("serverTime is null");
        }
        this.serverTime = serverTime;
    }

    @Override
    protected void send(NodeResponse response) throws IOException {
        response.setTimeStamp(serverTime.getTime());
        super.send(response);
    }

}
