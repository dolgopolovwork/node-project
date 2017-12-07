package ru.babobka.nodebusiness.service;

import ru.babobka.nodeserials.NodeAuthRequest;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;


/**
 * Created by dolgopolov.a on 29.10.15.
 */
public class MasterAuthService implements AuthService {

    private final NodeUsersService usersService = Container.getInstance().get(NodeUsersService.class);

    private final SimpleLogger logger = Container.getInstance().get(SimpleLogger.class);

    @Override
    public boolean auth(NodeConnection connection) {
        try {
            NodeAuthRequest authRequest = connection.receive();
            String hashedPassword = authRequest.getHashedPassword();
            String login = authRequest.getLogin();
            boolean authSuccess = usersService.auth(login, hashedPassword);
            connection.send(authSuccess);
            return authSuccess;
        } catch (IOException e) {
            logger.error(e);
        }
        return false;
    }

}
