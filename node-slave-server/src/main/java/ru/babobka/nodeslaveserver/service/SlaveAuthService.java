package ru.babobka.nodeslaveserver.service;

import ru.babobka.nodesecurity.auth.AbstractAuth;
import ru.babobka.nodesecurity.auth.AuthData;
import ru.babobka.nodesecurity.auth.AuthResult;
import ru.babobka.nodesecurity.config.SrpConfig;
import ru.babobka.nodesecurity.rsa.RSAPublicKey;
import ru.babobka.nodesecurity.service.RSAService;
import ru.babobka.nodesecurity.service.SRPService;
import ru.babobka.nodeslaveserver.server.SlaveServerConfig;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.NodeLogger;
import ru.babobka.nodeutils.math.Fp;
import ru.babobka.nodeutils.network.NodeConnection;
import ru.babobka.nodeutils.util.HashUtil;
import ru.babobka.nodeutils.util.MathUtil;

import java.io.IOException;
import java.math.BigInteger;

/**
 * Created by 123 on 30.04.2018.
 */
public class SlaveAuthService extends AbstractAuth {
    private final NodeLogger nodeLogger = Container.getInstance().get(NodeLogger.class);
    private final SRPService srpService = Container.getInstance().get(SRPService.class);
    private final SlaveServerConfig slaveServerConfig = Container.getInstance().get(SlaveServerConfig.class);
    private final RSAService rsaService = Container.getInstance().get(RSAService.class);

    public AuthResult authClient(NodeConnection connection, String login, String password) throws IOException {
        connection.send(login);
        boolean ableToLogin = connection.receive();
        if (!ableToLogin) {
            nodeLogger.debug("cannot login as " + login);
            return AuthResult.fail();
        }
        return srpUserAuth(connection, login, password);
    }

    public boolean authServer(NodeConnection connection) throws IOException {
        RSAPublicKey publicKey = slaveServerConfig.getServerPublicKey();
        BigInteger nonce = MathUtil.createNonce(publicKey.getN().bitLength());
        BigInteger encryptedNonce = rsaService.encrypt(nonce, publicKey);
        connection.send(encryptedNonce);
        BigInteger decryptedNonce = connection.receive();
        boolean success = nonce.equals(decryptedNonce);
        connection.send(success);
        return success;
    }

    AuthResult srpUserAuth(NodeConnection connection, String login, String password) throws IOException {
        AuthData authData = connection.receive();
        SrpConfig srpConfig = authData.getSrpConfig();
        Fp a = new Fp(srpService.generatePrivateKey(authData.getSrpConfig()), srpConfig.getG().getMod());
        Fp A = authData.getSrpConfig().getG().pow(a.getNumber());
        connection.send(A);
        boolean validA = connection.receive();
        if (!validA) {
            nodeLogger.debug("failed A validation from server: " + A);
            return AuthResult.fail();
        }
        Fp B = connection.receive();
        if (!B.isSameMod(srpConfig.getG()) || B.isMultNeutral() || B.isAddNeutral()) {
            nodeLogger.debug("invalid B :" + B);
            return fail(connection);
        }
        nodeLogger.debug("server's B is fine");
        success(connection);
        Fp u = new Fp(new BigInteger(HashUtil.sha2(A.getNumber().toByteArray(), B.getNumber().toByteArray())), srpConfig.getG().getMod());
        byte[] hashedPassword = HashUtil.sha2(password);
        byte[] secret = HashUtil.sha2(hashedPassword, authData.getSalt());
        Fp x = new Fp(new BigInteger(secret), srpConfig.getG().getMod());
        byte[] secretKey = srpService.createSecretKeyUser(B, a, u, x, srpConfig);
        boolean validKeys = checkSecretKeys(connection, secretKey, authData);
        if (!validKeys) {
            return fail(connection);
        }
        success(connection);
        return AuthResult.success(login, secretKey);
    }

    private boolean checkSecretKeys(NodeConnection connection, byte[] secretKey, AuthData authData) throws IOException {
        boolean solvedChallenge = srpService.solveChallenge(connection, secretKey);
        if (!solvedChallenge) {
            nodeLogger.debug("failed to solve challenge");
            return false;
        }
        boolean challengeResult = srpService.sendChallenge(connection, secretKey, authData.getSrpConfig());
        if (!challengeResult) {
            nodeLogger.debug("server failed to solve challenge");
            return false;
        }
        return true;
    }
}