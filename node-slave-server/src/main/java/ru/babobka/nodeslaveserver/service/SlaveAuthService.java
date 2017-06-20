package ru.babobka.nodeslaveserver.service;

import ru.babobka.nodeslaveserver.builder.AuthResponseBuilder;
import ru.babobka.nodeslaveserver.exception.MasterServerIsFullException;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.util.StreamUtil;
import ru.babobka.nodeslaveserver.server.SlaveServerConfig;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.crypto.PublicKey;
import ru.babobka.nodeserials.crypto.RSA;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by dolgopolov.a on 30.10.15.
 */
public class SlaveAuthService implements AuthService {

    private final SimpleLogger logger = Container.getInstance().get(SimpleLogger.class);

    private final SlaveServerConfig slaveServerConfig = Container.getInstance().get(SlaveServerConfig.class);

    @Override
    public boolean auth(Socket socket, String login, String password) throws IOException {
        socket.setSoTimeout(slaveServerConfig.getAuthTimeoutMillis());
        boolean fittable = StreamUtil.receiveObject(socket);
        if (fittable) {
            PublicKey publicKey = StreamUtil.receiveObject(socket);
            NodeResponse response = AuthResponseBuilder.build(new RSA(null, publicKey), login, password);
            StreamUtil.sendObject(response, socket);
            return (Boolean) StreamUtil.receiveObject(socket);
        } else {
            logger.warning("Can not connect to master server due to connection limit");
            throw new MasterServerIsFullException();

        }

    }
}
