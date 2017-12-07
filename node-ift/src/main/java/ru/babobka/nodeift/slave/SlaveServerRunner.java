package ru.babobka.nodeift.slave;

import ru.babobka.nodeslaveserver.server.SlaveServer;
import ru.babobka.nodeslaveserver.server.SlaveServerConfig;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.network.NodeConnection;
import ru.babobka.nodeutils.util.HashUtil;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by 123 on 05.11.2017.
 */
public class SlaveServerRunner {
    public static void init() {
        Container.getInstance().put(new SlaveServerApplicationContainer());
    }

    public static SlaveServer runSlaveServer(String login, String password) throws IOException {
        SlaveServer slaveServer = getSlaveServer(login, HashUtil.hexSha2(password));
        slaveServer.start();
        return slaveServer;
    }

    static SlaveServer getSlaveServer(String login, String password) throws IOException {
        SlaveServerConfig slaveServerConfig = Container.getInstance().get(SlaveServerConfig.class);
        NodeConnection connection = new NodeConnection(new Socket(slaveServerConfig.getServerHost(), slaveServerConfig.getServerPort()));
        return new SlaveServer(connection, login, password);
    }

}
