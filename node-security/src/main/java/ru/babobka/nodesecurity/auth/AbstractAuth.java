package ru.babobka.nodesecurity.auth;

import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;

/**
 * Created by 123 on 03.05.2018.
 */
public abstract class AbstractAuth {
    protected AuthResult fail(NodeConnection connection) throws IOException {
        connection.send(false);
        return AuthResult.fail();
    }

    protected void success(NodeConnection connection) throws IOException {
        connection.send(true);
    }
}
