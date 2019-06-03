package ru.babobka.nodemasterserver.service;

import org.apache.log4j.Logger;
import ru.babobka.nodebusiness.model.User;
import ru.babobka.nodebusiness.service.NodeUsersService;
import ru.babobka.nodeconfigs.master.MasterServerConfig;
import ru.babobka.nodemasterserver.slave.Sessions;
import ru.babobka.nodesecurity.auth.AuthHelper;
import ru.babobka.nodesecurity.auth.AuthResult;
import ru.babobka.nodesecurity.data.SecureNodeResponse;
import ru.babobka.nodesecurity.exception.NodeSecurityException;
import ru.babobka.nodesecurity.keypair.KeyDecoder;
import ru.babobka.nodesecurity.network.SecureNodeConnection;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;
import java.security.spec.InvalidKeySpecException;
import java.util.UUID;


/**
 * Created by dolgopolov.a on 29.10.15.
 */
public class MasterAuthService extends AuthHelper {

    private static final Logger logger = Logger.getLogger(MasterAuthService.class);
    private final NodeUsersService usersService = Container.getInstance().get(NodeUsersService.class);
    private final Sessions sessions = Container.getInstance().get(Sessions.class);
    private final MasterServerConfig masterServerConfig = Container.getInstance().get(MasterServerConfig.class);

    public AuthResult authClient(NodeConnection connection) throws IOException {
        String login = connection.receive();
        if (sessions.contains(login)) {
            logger.error(login + " has been authenticated");
            return fail(connection);
        }
        User user = usersService.get(login);
        if (user == null) {
            logger.error("can not find user " + login);
            return fail(connection);
        }
        logger.info(login + " was found");
        success(connection);
        return checkUser(connection, user);
    }

    private AuthResult checkUser(NodeConnection connection, User user) throws IOException {
        try {
            SecureNodeConnection secureNodeConnection = new SecureNodeConnection(
                    connection, KeyDecoder.decodePrivateKey(masterServerConfig.getKeyPair().getPrivKey()), user.getPublicKey());
            UUID nonceId = UUID.randomUUID();
            secureNodeConnection.send(NodeResponse.dummy(nonceId));
            SecureNodeResponse userResponse = secureNodeConnection.receiveNoClose();
            if (!userResponse.getTaskId().equals(nonceId)) {
                return fail(connection);
            }
            success(connection);
            SecureNodeResponse userNonceChallenge = secureNodeConnection.receiveNoClose();
            secureNodeConnection.send(userNonceChallenge);
            if (connection.receive()) {
                return AuthResult.success(user.getName(), user.getPublicKey());
            } else {
                return AuthResult.fail();
            }
        } catch (NodeSecurityException | InvalidKeySpecException e) {
            logger.error("failed to auth user '" + user.getName() + "'", e);
            return fail(connection);
        }
    }

}
