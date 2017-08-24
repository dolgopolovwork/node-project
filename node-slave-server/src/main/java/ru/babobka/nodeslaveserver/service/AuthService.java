package ru.babobka.nodeslaveserver.service;

import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;

/**
 * Created by dolgopolov.a on 30.10.15.
 */
public interface AuthService {

    boolean auth(NodeConnection connection, String login, String password) throws IOException;
}
