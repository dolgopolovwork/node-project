package ru.babobka.nodetester.slave;

import ru.babobka.nodeslaveserver.server.SlaveServer;
import ru.babobka.nodeslaveserver.server.SlaveServerConfig;
import ru.babobka.nodeutils.container.Container;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by 123 on 05.11.2017.
 */
public class SlaveServerRunner {
    public static SlaveServerApplicationContainer init() {
        SlaveServerApplicationContainer container = new SlaveServerApplicationContainer();
        Container.getInstance().put(container);
        return container;
    }

    public static SlaveServer runSlaveServer(String login, String password) throws IOException {
        SlaveServer slaveServer = getSlaveServer(login, password);
        slaveServer.start();
        return slaveServer;
    }

    public static SlaveServer getSlaveServer(String login, String password) throws IOException {
        SlaveServerConfig slaveServerConfig = Container.getInstance().get(SlaveServerConfig.class);
        Socket socket = new Socket(slaveServerConfig.getServerHost(), slaveServerConfig.getServerPort());
        return new SlaveServer(socket, login, password);
    }
}
