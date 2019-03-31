package ru.babobka.slavenoderun.factory;

import ru.babobka.nodeslaveserver.server.SlaveServer;
import ru.babobka.nodeslaveserver.server.SlaveServerFactory;

import java.io.IOException;
import java.net.Socket;

public class PlainSlaveServerFactory extends SlaveServerRunnerFactory {

    @Override
    public SlaveServer create(String masterHost,
                             int masterPort,
                             String login,
                             String password) throws IOException {
        return SlaveServerFactory.slaveBacked(
                new Socket(masterHost, masterPort), login, password);
    }
}
