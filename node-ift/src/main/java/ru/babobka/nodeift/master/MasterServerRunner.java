package ru.babobka.nodeift.master;

import ru.babobka.nodemasterserver.server.MasterServer;
import ru.babobka.nodeutils.container.Container;

/**
 * Created by 123 on 06.11.2017.
 */
public class MasterServerRunner {

    public static void init() {
        Container.getInstance().put(new MasterServerApplicationContainer());
    }

    public static MasterServer runMasterServer() {
        MasterServer masterServer = new MasterServer();
        masterServer.start();
        return masterServer;
    }
}
