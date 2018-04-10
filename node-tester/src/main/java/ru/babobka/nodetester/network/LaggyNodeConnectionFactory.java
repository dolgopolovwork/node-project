package ru.babobka.nodetester.network;

import ru.babobka.nodeutils.network.NodeConnection;
import ru.babobka.nodeutils.network.NodeConnectionFactory;

import java.net.Socket;

/**
 * Created by 123 on 06.04.2018.
 */
public class LaggyNodeConnectionFactory extends NodeConnectionFactory {

    @Override
    public NodeConnection create(Socket socket) {
        return new LaggyNodeConnection(socket);
    }
}
