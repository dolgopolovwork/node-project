package ru.babobka.nodeslaveserver.service;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by dolgopolov.a on 30.10.15.
 */
public interface AuthService {

    boolean auth(Socket socket, String login, String password) throws IOException;
}
