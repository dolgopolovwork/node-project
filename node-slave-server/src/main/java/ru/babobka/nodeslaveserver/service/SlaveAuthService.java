package ru.babobka.nodeslaveserver.service;

import ru.babobka.nodesecurity.auth.AbstractAuth;
import ru.babobka.nodesecurity.auth.AuthData;
import ru.babobka.nodesecurity.auth.AuthResult;
import ru.babobka.nodesecurity.config.SrpConfig;
import ru.babobka.nodesecurity.service.SecurityService;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.math.Fp;
import ru.babobka.nodeutils.network.NodeConnection;
import ru.babobka.nodeutils.util.HashUtil;

import java.io.IOException;
import java.math.BigInteger;

/**
 * Created by 123 on 30.04.2018.
 */
public class SlaveAuthService extends AbstractAuth {
    private final SimpleLogger logger = Container.getInstance().get(SimpleLogger.class);
    private final SecurityService securityService = Container.getInstance().get(SecurityService.class);

    public AuthResult auth(NodeConnection connection, String login, String password) throws IOException {
        connection.send(login);
        boolean userFound = connection.receive();
        if (!userFound) {
            logger.debug(login + " was not found");
            return AuthResult.fail();
        }
        return srpUserAuth(connection, password);
    }

    AuthResult srpUserAuth(NodeConnection connection, String password) throws IOException {
        AuthData authData = connection.receive();
        SrpConfig srpConfig = authData.getSrpConfig();
        Fp a = new Fp(securityService.generatePrivateKey(authData.getSrpConfig()), srpConfig.getG().getMod());
        Fp A = authData.getSrpConfig().getG().pow(a.getNumber());
        connection.send(A);
        boolean validA = connection.receive();
        if (!validA) {
            logger.debug("failed A validation from server: " + A);
            return AuthResult.fail();
        }
        Fp B = connection.receive();
        if (!B.isSameMod(srpConfig.getG()) || B.isMultNeutral() || B.isAddNeutral()) {
            logger.debug("invalid B :" + B);
            return fail(connection);
        } else {
            logger.debug("server's B is fine");
            success(connection);
        }
        Fp u = new Fp(new BigInteger(HashUtil.sha2(A.getNumber().toByteArray(), B.getNumber().toByteArray())), srpConfig.getG().getMod());
        byte[] hashedPassword = HashUtil.sha2(password);
        byte[] secret = HashUtil.sha2(hashedPassword, authData.getSalt());
        Fp x = new Fp(new BigInteger(secret), srpConfig.getG().getMod());
        byte[] secretKey = securityService.createSecretKeyUser(B, a, u, x, srpConfig);
        boolean solvedChallenge = securityService.solveChallenge(connection, secretKey);
        if (!solvedChallenge) {
            logger.debug("failed to solve challenge");
            return AuthResult.fail();
        }
        boolean challengeResult = securityService.sendChallenge(connection, secretKey, authData.getSrpConfig());
        if (!challengeResult) {
            logger.debug("server failed to solve challenge");
            return AuthResult.fail();
        } else {
            success(connection);
            return AuthResult.success(secretKey);
        }
    }

}
