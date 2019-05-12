package ru.babobka.nodeslaveserver.service;

import lombok.NonNull;
import org.apache.log4j.Logger;
import ru.babobka.nodeconfigs.slave.SlaveServerConfig;
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
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.UUID;

/**
 * Created by 123 on 30.04.2018.
 */
public class SlaveAuthService extends AuthHelper {
    private static final Logger logger = Logger.getLogger(SlaveAuthService.class);
    private final SlaveServerConfig slaveServerConfig = Container.getInstance().get(SlaveServerConfig.class);

    public AuthResult authClient(@NonNull NodeConnection connection,
                                 @NonNull String login,
                                 @NonNull PrivateKey privateKey) throws IOException {
        connection.send(login);
        boolean ableToLogin = connection.receive();
        if (!ableToLogin) {
            logger.error("cannot login as '" + login + "'");
            return AuthResult.fail();
        }
        return checkServer(connection, login, privateKey);
    }

    private AuthResult checkServer(
            @NonNull NodeConnection connection, @NonNull String login, @NonNull PrivateKey privateKey) throws IOException {
        try {
            PublicKey serverPubKey = KeyDecoder.decodePublicKey(slaveServerConfig.getServerBase64PublicKey());
            SecureNodeConnection secureNodeConnection = new SecureNodeConnection(connection, privateKey, serverPubKey);
            SecureNodeResponse serverNonceChallenge = secureNodeConnection.receiveNoClose();
            secureNodeConnection.send(serverNonceChallenge);
            if (!(boolean) connection.receive()) {
                return AuthResult.fail();
            }
            UUID nonceId = UUID.randomUUID();
            secureNodeConnection.send(NodeResponse.dummy(nonceId));
            SecureNodeResponse serverResponse = secureNodeConnection.receiveNoClose();
            if (serverResponse.getTaskId().equals(nonceId)) {
                success(connection);
                return AuthResult.success(login, serverPubKey);
            } else {
                return fail(connection);
            }
        } catch (NodeSecurityException | InvalidKeySpecException e) {
            logger.error("cannot authenticate slave", e);
            return fail(connection);
        }
    }

}
