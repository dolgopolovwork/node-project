package ru.babobka.nodeslaveserver.service;

import ru.babobka.nodeserials.NodeAuthRequest;
import ru.babobka.nodeslaveserver.server.SlaveServerConfig;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;

/**
 * Created by dolgopolov.a on 30.10.15.
 */
public class SlaveAuthService implements AuthService {

    private final SlaveServerConfig slaveServerConfig = Container.getInstance().get(SlaveServerConfig.class);

    @Override
    public boolean auth(NodeConnection connection, String login, String password) throws IOException {
        connection.setReadTimeOut(slaveServerConfig.getAuthTimeoutMillis());
        connection.send(new NodeAuthRequest(login, password));
        return connection.receive();
    }
}
