package ru.babobka.nodetester.slave;

import ru.babobka.nodeconfigs.slave.SlaveServerConfig;
import ru.babobka.nodeslaveserver.server.SlaveServer;
import ru.babobka.nodeslaveserver.server.SlaveServerFactory;
import ru.babobka.nodeutils.container.Container;

import java.io.IOException;
import java.net.Socket;
import java.security.PublicKey;

/**
 * Created by 123 on 05.11.2017.
 */
public class SlaveServerRunner {
    public static TesterSlaveServerApplicationContainer init(PublicKey publicKey) {
        TesterSlaveServerApplicationContainer container = new TesterSlaveServerApplicationContainer(publicKey);
        Container.getInstance().put(container);
        return container;
    }

    public static SlaveServer runSlaveServer(String login) throws IOException {
        SlaveServer slaveServer = getSlaveServer(login);
        slaveServer.start();
        return slaveServer;
    }

    public static SlaveServer getSlaveServer(String login) throws IOException {
        SlaveServerConfig slaveServerConfig = Container.getInstance().get(SlaveServerConfig.class);
        Socket socket = new Socket(slaveServerConfig.getMasterServerHost(), slaveServerConfig.getMasterServerPort());
        return SlaveServerFactory.slaveBacked(socket, login, slaveServerConfig.getWebPort());
    }
}
