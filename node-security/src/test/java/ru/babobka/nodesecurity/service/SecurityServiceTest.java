package ru.babobka.nodesecurity.service;

import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodesecurity.config.SrpConfig;
import ru.babobka.nodesecurity.data.SecureDataFactory;
import ru.babobka.nodesecurity.data.SecureNodeRequest;
import ru.babobka.nodesecurity.data.SecureNodeResponse;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.math.Fp;
import ru.babobka.nodeutils.network.NodeConnection;
import ru.babobka.nodeutils.util.HashUtil;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by 123 on 29.04.2018.
 */
public class SecurityServiceTest {
    private SecurityService securityService;
    private SecureDataFactory secureDataFactory;
    private byte[] secretKey = {1, 2, 3, 4, 5, 6, 7, 8, 9};

    @Before
    public void setUp() {
        securityService = spy(new SecurityService());
        Container.getInstance().put(securityService);
        secureDataFactory = new SecureDataFactory();
    }

    @Test
    public void testIsSecureBadObject() {
        assertFalse(securityService.isSecure("test", secretKey));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsSecureNullObject() {
        assertFalse(securityService.isSecure(null, secretKey));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsSecureNullSecretKey() {
        assertFalse(securityService.isSecure("test", null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsSecureEmptySecretKey() {
        assertFalse(securityService.isSecure("test", new byte[]{}));
    }

    @Test
    public void testIsSecureRequestHeartBeating() {
        NodeRequest nodeRequest = NodeRequest.heartBeat();
        assertTrue(securityService.isSecure(nodeRequest, secretKey));
    }

    @Test
    public void testIsSecureResponseHeartBeating() {
        NodeResponse nodeResponse = NodeResponse.heartBeat();
        assertTrue(securityService.isSecure(nodeResponse, secretKey));
    }

    @Test
    public void testIsSecureRequest() {
        SecureNodeRequest request = secureDataFactory.create(NodeRequest.stop(UUID.randomUUID()), secretKey);
        assertTrue(securityService.isSecure(request, secretKey));
    }

    @Test
    public void testIsSecureRequestDifferentKey() {
        SecureNodeRequest request = secureDataFactory.create(NodeRequest.stop(UUID.randomUUID()), new byte[]{1, 2, 3});
        assertFalse(securityService.isSecure(request, secretKey));
    }


    @Test
    public void testIsSecureResponse() {
        SecureNodeResponse response = secureDataFactory.create(NodeResponse.stopped(UUID.randomUUID()), secretKey);
        assertTrue(securityService.isSecure(response, secretKey));
    }

    @Test
    public void testIsSecureResponseDifferentKey() {
        SecureNodeResponse response = secureDataFactory.create(NodeResponse.stopped(UUID.randomUUID()), secretKey);
        assertFalse(securityService.isSecure(response, new byte[]{1, 2, 3}));
    }

    @Test
    public void testIsSecureResponseBadMac() {
        SecureNodeResponse response = spy(secureDataFactory.create(NodeResponse.stopped(UUID.randomUUID()), secretKey));
        doReturn(new byte[]{1, 2, 3}).when(response).getMac();
        assertFalse(securityService.isSecure(response, secretKey));
    }

    @Test
    public void testIsSecureRequestBadMac() {
        SecureNodeRequest request = spy(secureDataFactory.create(SecureNodeRequest.stop(UUID.randomUUID()), secretKey));
        doReturn(new byte[]{1, 2, 3}).when(request).getMac();
        assertFalse(securityService.isSecure(request, secretKey));
    }

    @Test
    public void testBuildMacSameObject() {
        NodeRequest request = SecureNodeRequest.stop(UUID.randomUUID());
        assertArrayEquals(securityService.buildMac(request, secretKey), securityService.buildMac(request, secretKey));
    }

    @Test
    public void testBuildMacSameObjectDifferentKeys() {
        NodeRequest request = SecureNodeRequest.stop(UUID.randomUUID());
        assertFalse(Arrays.equals(securityService.buildMac(request, new byte[]{1, 2, 3}), securityService.buildMac(request, secretKey)));
    }

    @Test
    public void testIsSecurePlainNodeResponse() {
        assertFalse(securityService.isSecure(NodeResponse.stopped(UUID.randomUUID()), secretKey));
    }

    @Test
    public void testIsSecurePlainNodeRequest() {
        assertFalse(securityService.isSecure(NodeRequest.stop(UUID.randomUUID()), secretKey));
    }

    @Test
    public void testSecretBuilder() {
        byte[] password = {1, 2, 3};
        byte[] salt = {4, 5, 6};
        Fp gen = new Fp(BigInteger.ONE, BigInteger.TEN);
        SrpConfig srpConfig = new SrpConfig(gen, 2);
        byte[] secret = securityService.secretBuilder(password, salt, srpConfig);
        byte[] hashedPassword = HashUtil.sha2(password, salt);
        BigInteger numPassword = new BigInteger(hashedPassword);
        byte[] expected = srpConfig.getG().pow(numPassword.mod(srpConfig.getG().getMod())).getNumber().toByteArray();
        assertArrayEquals(secret, expected);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSendChallengeNullConnection() throws IOException {
        Fp gen = new Fp(BigInteger.ONE, BigInteger.TEN);
        SrpConfig srpConfig = new SrpConfig(gen, 2);
        securityService.sendChallenge(null, new byte[]{1, 2, 3}, srpConfig);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSendChallengeNullSecretKey() throws IOException {
        Fp gen = new Fp(BigInteger.ONE, BigInteger.TEN);
        SrpConfig srpConfig = new SrpConfig(gen, 2);
        securityService.sendChallenge(mock(NodeConnection.class), null, srpConfig);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSendChallengeEmptyKey() throws IOException {
        Fp gen = new Fp(BigInteger.ONE, BigInteger.TEN);
        SrpConfig srpConfig = new SrpConfig(gen, 2);
        securityService.sendChallenge(mock(NodeConnection.class), new byte[]{}, srpConfig);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSendChallengeNullConfig() throws IOException {
        securityService.sendChallenge(mock(NodeConnection.class), new byte[]{1, 2, 3}, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSendChallengeClosedConnection() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.isClosed()).thenReturn(true);
        Fp gen = new Fp(BigInteger.ONE, BigInteger.TEN);
        SrpConfig srpConfig = new SrpConfig(gen, 2);
        securityService.sendChallenge(connection, new byte[]{1, 2, 3}, srpConfig);
    }

    @Test
    public void testSendChallenge() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.isClosed()).thenReturn(false);
        Fp gen = new Fp(BigInteger.ONE, BigInteger.TEN);
        SrpConfig srpConfig = new SrpConfig(gen, 2);
        byte[] challenge = {1, 2, 3};
        byte[] secret = {4, 5, 6};
        when(securityService.createChallenge(srpConfig)).thenReturn(challenge);
        when(connection.receive()).thenReturn(HashUtil.sha2(secret, challenge));
        assertTrue(securityService.sendChallenge(connection, secret, srpConfig));
    }

    @Test
    public void testSendChallengeFailed() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.isClosed()).thenReturn(false);
        Fp gen = new Fp(BigInteger.ONE, BigInteger.TEN);
        SrpConfig srpConfig = new SrpConfig(gen, 2);
        byte[] challenge = {1, 2, 3};
        byte[] secret = {4, 5, 6};
        when(securityService.createChallenge(srpConfig)).thenReturn(challenge);
        when(connection.receive()).thenReturn(new byte[]{7, 8, 9});
        assertFalse(securityService.sendChallenge(connection, secret, srpConfig));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSolveChallengeNullConnection() throws IOException {
        securityService.solveChallenge(null, new byte[]{1, 2, 3});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSolveChallengeClosedConnection() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.isClosed()).thenReturn(true);
        securityService.solveChallenge(connection, new byte[]{1, 2, 3});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSolveChallengeNullSecretKey() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.isClosed()).thenReturn(false);
        securityService.solveChallenge(connection, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSolveChallengeEmptySecretKey() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.isClosed()).thenReturn(false);
        securityService.solveChallenge(connection, new byte[]{});
    }

    @Test
    public void testSolveChallenge() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.isClosed()).thenReturn(false);
        byte[] secretKey = {1, 2, 3};
        byte[] receivedChallenge = {4, 5, 6};
        when(connection.receive()).thenReturn(receivedChallenge, true);
        assertTrue(securityService.solveChallenge(connection, secretKey));
        verify(connection).send(HashUtil.sha2(secretKey, receivedChallenge));
    }

    @Test
    public void testSolveChallengeFail() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.isClosed()).thenReturn(false);
        byte[] secretKey = {1, 2, 3};
        byte[] receivedChallenge = {4, 5, 6};
        when(connection.receive()).thenReturn(receivedChallenge, false);
        assertFalse(securityService.solveChallenge(connection, secretKey));
        verify(connection).send(HashUtil.sha2(secretKey, receivedChallenge));
    }
}

