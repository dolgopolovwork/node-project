package ru.babobka.nodeutils.network;

import java.net.Socket;

/**
 * Created by 123 on 19.09.2017.
 */
public class NodeConnectionFactory {

    public NodeConnection create(Socket socket) {
        return new NodeConnection(socket);
    }
}
