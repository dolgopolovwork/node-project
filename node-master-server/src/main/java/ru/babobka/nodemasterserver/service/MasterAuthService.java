package ru.babobka.nodemasterserver.service;

import java.math.BigInteger;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.util.StreamUtil;
import ru.babobka.nodemasterserver.model.AuthResult;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.crypto.RSA;

/**
 * Created by dolgopolov.a on 29.10.15.
 */
public class MasterAuthService implements AuthService {

    private final NodeUsersService usersService = Container.getInstance().get(NodeUsersService.class);

    private final SimpleLogger logger = Container.getInstance().get(SimpleLogger.class);

    @Override
    public AuthResult getAuthResult(RSA rsa, Socket socket) {
        try {
            StreamUtil.sendObject(rsa.getPublicKey(), socket);
            NodeResponse authResponse = StreamUtil.receiveObject(socket);
            if (authResponse.isAuthResponse()) {
                BigInteger integerHashedPassword = rsa.decrypt(authResponse.getDataValue("password"));
                String login = authResponse.getDataValue("login");
                List<String> tasksList = authResponse.getDataValue("tasksList");
                if (tasksList != null && !tasksList.isEmpty()) {
                    Set<String> taskSet = new HashSet<>(tasksList);
                    boolean authSuccess = usersService.auth(login, integerHashedPassword);
                    StreamUtil.sendObject(authSuccess, socket);
                    if (authSuccess) {
                        return new AuthResult(true, login, taskSet);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e);
        }
        return AuthResult.failed();
    }
}
