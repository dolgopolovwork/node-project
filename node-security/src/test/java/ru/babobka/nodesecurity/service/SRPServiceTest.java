package ru.babobka.nodesecurity.service;

import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodesecurity.config.SrpConfig;
import ru.babobka.nodesecurity.data.SecureDataFactory;
import ru.babobka.nodesecurity.data.SecureNodeRequest;
import ru.babobka.nodesecurity.data.SecureNodeResponse;
import ru.babobka.nodeserials.NodeData;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.data.Data;
import ru.babobka.nodeserials.enumerations.ResponseStatus;
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
public class SRPServiceTest {
    private SRPService srpService;
    private SecureDataFactory secureDataFactory;
    private byte[] secretKey = {1, 2, 3, 4, 5, 6, 7, 8, 9};

    @Before
    public void setUp() {
        srpService = spy(new SRPService());
        Container.getInstance().put(srpService);
        secureDataFactory = new SecureDataFactory();
    }

    @Test
    public void testIsSecureBadObject() {
        assertFalse(srpService.isSecure("test", secretKey));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsSecureNullObject() {
        assertFalse(srpService.isSecure(null, secretKey));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsSecureNullSecretKey() {
        assertFalse(srpService.isSecure("test", null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsSecureEmptySecretKey() {
        assertFalse(srpService.isSecure("test", new byte[]{}));
    }

    @Test
    public void testIsSecureRequestHeartBeating() {
        NodeRequest nodeRequest = NodeRequest.heartBeat();
        assertTrue(srpService.isSecure(nodeRequest, secretKey));
    }

    @Test
    public void testIsSecureResponseHeartBeating() {
        NodeResponse nodeResponse = NodeResponse.heartBeat();
        assertTrue(srpService.isSecure(nodeResponse, secretKey));
    }

    @Test
    public void testIsSecureRequest() {
        SecureNodeRequest request = secureDataFactory.create(NodeRequest.stop(UUID.randomUUID()), secretKey);
        assertTrue(srpService.isSecure(request, secretKey));
    }

    @Test
    public void testIsSecureRequestDifferentKey() {
        SecureNodeRequest request = secureDataFactory.create(NodeRequest.stop(UUID.randomUUID()), new byte[]{1, 2, 3});
        assertFalse(srpService.isSecure(request, secretKey));
    }


    @Test
    public void testIsSecureResponse() {
        SecureNodeResponse response = secureDataFactory.create(NodeResponse.stopped(UUID.randomUUID()), secretKey);
        assertTrue(srpService.isSecure(response, secretKey));
    }

    @Test
    public void testIsSecureResponseDifferentKey() {
        SecureNodeResponse response = secureDataFactory.create(NodeResponse.stopped(UUID.randomUUID()), secretKey);
        assertFalse(srpService.isSecure(response, new byte[]{1, 2, 3}));
    }

    @Test
    public void testIsSecureResponseBadMac() {
        SecureNodeResponse response = spy(secureDataFactory.create(NodeResponse.stopped(UUID.randomUUID()), secretKey));
        doReturn(new byte[]{1, 2, 3}).when(response).getMac();
        assertFalse(srpService.isSecure(response, secretKey));
    }

    @Test
    public void testIsSecureRequestBadMac() {
        SecureNodeRequest request = spy(secureDataFactory.create(SecureNodeRequest.stop(UUID.randomUUID()), secretKey));
        doReturn(new byte[]{1, 2, 3}).when(request).getMac();
        assertFalse(srpService.isSecure(request, secretKey));
    }

    @Test
    public void testBuildMacSameObject() {
        NodeRequest request = SecureNodeRequest.stop(UUID.randomUUID());
        assertArrayEquals(srpService.buildMac(request, secretKey), srpService.buildMac(request, secretKey));
    }

    @Test
    public void testBuildMacSameObjectDifferentKeys() {
        NodeRequest request = SecureNodeRequest.stop(UUID.randomUUID());
        assertFalse(Arrays.equals(srpService.buildMac(request, new byte[]{1, 2, 3}), srpService.buildMac(request, secretKey)));
    }

    @Test
    public void testIsSecurePlainNodeResponse() {
        assertFalse(srpService.isSecure(NodeResponse.stopped(UUID.randomUUID()), secretKey));
    }

    @Test
    public void testIsSecurePlainNodeRequest() {
        assertFalse(srpService.isSecure(NodeRequest.stop(UUID.randomUUID()), secretKey));
    }

    @Test
    public void testSecretBuilder() {
        byte[] password = {1, 2, 3};
        byte[] salt = {4, 5, 6};
        Fp gen = new Fp(BigInteger.ONE, BigInteger.TEN);
        SrpConfig srpConfig = new SrpConfig(gen, 2);
        byte[] secret = srpService.secretBuilder(password, salt, srpConfig);
        byte[] hashedPassword = HashUtil.sha2(password, salt);
        BigInteger numPassword = new BigInteger(hashedPassword);
        byte[] expected = srpConfig.getG().pow(numPassword.mod(srpConfig.getG().getMod())).getNumber().toByteArray();
        assertArrayEquals(secret, expected);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSendChallengeNullConnection() throws IOException {
        Fp gen = new Fp(BigInteger.ONE, BigInteger.TEN);
        SrpConfig srpConfig = new SrpConfig(gen, 2);
        srpService.sendChallenge(null, new byte[]{1, 2, 3}, srpConfig);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSendChallengeNullSecretKey() throws IOException {
        Fp gen = new Fp(BigInteger.ONE, BigInteger.TEN);
        SrpConfig srpConfig = new SrpConfig(gen, 2);
        srpService.sendChallenge(mock(NodeConnection.class), null, srpConfig);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSendChallengeEmptyKey() throws IOException {
        Fp gen = new Fp(BigInteger.ONE, BigInteger.TEN);
        SrpConfig srpConfig = new SrpConfig(gen, 2);
        srpService.sendChallenge(mock(NodeConnection.class), new byte[]{}, srpConfig);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSendChallengeNullConfig() throws IOException {
        srpService.sendChallenge(mock(NodeConnection.class), new byte[]{1, 2, 3}, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSendChallengeClosedConnection() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.isClosed()).thenReturn(true);
        Fp gen = new Fp(BigInteger.ONE, BigInteger.TEN);
        SrpConfig srpConfig = new SrpConfig(gen, 2);
        srpService.sendChallenge(connection, new byte[]{1, 2, 3}, srpConfig);
    }

    @Test
    public void testSendChallenge() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.isClosed()).thenReturn(false);
        Fp gen = new Fp(BigInteger.ONE, BigInteger.TEN);
        SrpConfig srpConfig = new SrpConfig(gen, 2);
        byte[] challenge = {1, 2, 3};
        byte[] secret = {4, 5, 6};
        when(srpService.createChallenge(srpConfig)).thenReturn(challenge);
        when(connection.receive()).thenReturn(HashUtil.sha2(secret, challenge));
        assertTrue(srpService.sendChallenge(connection, secret, srpConfig));
    }

    @Test
    public void testSendChallengeFailed() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.isClosed()).thenReturn(false);
        Fp gen = new Fp(BigInteger.ONE, BigInteger.TEN);
        SrpConfig srpConfig = new SrpConfig(gen, 2);
        byte[] challenge = {1, 2, 3};
        byte[] secret = {4, 5, 6};
        when(srpService.createChallenge(srpConfig)).thenReturn(challenge);
        when(connection.receive()).thenReturn(new byte[]{7, 8, 9});
        assertFalse(srpService.sendChallenge(connection, secret, srpConfig));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSolveChallengeNullConnection() throws IOException {
        srpService.solveChallenge(null, new byte[]{1, 2, 3});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSolveChallengeClosedConnection() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.isClosed()).thenReturn(true);
        srpService.solveChallenge(connection, new byte[]{1, 2, 3});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSolveChallengeNullSecretKey() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.isClosed()).thenReturn(false);
        srpService.solveChallenge(connection, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSolveChallengeEmptySecretKey() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.isClosed()).thenReturn(false);
        srpService.solveChallenge(connection, new byte[]{});
    }

    @Test
    public void testSolveChallenge() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.isClosed()).thenReturn(false);
        byte[] secretKey = {1, 2, 3};
        byte[] receivedChallenge = {4, 5, 6};
        when(connection.receive()).thenReturn(receivedChallenge, true);
        assertTrue(srpService.solveChallenge(connection, secretKey));
        verify(connection).send(HashUtil.sha2(secretKey, receivedChallenge));
    }

    @Test
    public void testSolveChallengeFail() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.isClosed()).thenReturn(false);
        byte[] secretKey = {1, 2, 3};
        byte[] receivedChallenge = {4, 5, 6};
        when(connection.receive()).thenReturn(receivedChallenge, false);
        assertFalse(srpService.solveChallenge(connection, secretKey));
        verify(connection).send(HashUtil.sha2(secretKey, receivedChallenge));
    }

    @Test
    public void testGetHashSameObject() {
        UUID id = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        String taskName = "testTask";
        long timeStamp = 0;
        Data data = new Data();
        NodeData nodeData = new NodeData(id, taskId, taskName, timeStamp, data);
        assertArrayEquals(srpService.buildHash(nodeData), srpService.buildHash(nodeData));
    }

    @Test
    public void testGetHashSameObjectWithData() {
        UUID id = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        String taskName = "testTask";
        long timeStamp = 0;
        Data data = new Data();
        data.put("abc", 123);
        data.put("xyz", "test");
        NodeData nodeData = new NodeData(id, taskId, taskName, timeStamp, data);
        assertArrayEquals(srpService.buildHash(nodeData), srpService.buildHash(nodeData));
    }


    @Test
    public void testGetHashTwoEqualObjects() {
        UUID id = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        String taskName = "testTask";
        long timeStamp = 0;
        Data data = new Data();
        data.put("abc", 123);
        data.put("xyz", "test");
        NodeData nodeData1 = new NodeData(id, taskId, taskName, timeStamp, data);
        NodeData nodeData2 = new NodeData(id, taskId, taskName, timeStamp, data);
        assertArrayEquals(srpService.buildHash(nodeData1), srpService.buildHash(nodeData2));
    }

    @Test
    public void testGetHashDifferentTime() {
        UUID id = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        String taskName = "testTask";
        long timeStamp = 0;
        Data data = new Data();
        data.put("abc", 123);
        data.put("xyz", "test");
        NodeData nodeData1 = new NodeData(id, taskId, taskName, timeStamp, data);
        NodeData nodeData2 = new NodeData(id, taskId, taskName, timeStamp + 1, data);
        assertFalse(Arrays.equals(srpService.buildHash(nodeData1), srpService.buildHash(nodeData2)));
    }

    @Test
    public void testGetHashDifferentData() {
        UUID id = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        String taskName = "testTask";
        long timeStamp = 0;
        Data data1 = new Data();
        data1.put("abc", 123);
        data1.put("xyz", "test");
        Data data2 = new Data();
        data2.put("abc", 456);
        data2.put("xyz", "test");
        NodeData nodeData1 = new NodeData(id, taskId, taskName, timeStamp, data1);
        NodeData nodeData2 = new NodeData(id, taskId, taskName, timeStamp, data2);
        assertFalse(Arrays.equals(srpService.buildHash(nodeData1), srpService.buildHash(nodeData2)));
    }

    @Test
    public void testGetHashSameData() {
        UUID id = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        String taskName = "testTask";
        long timeStamp = 0;
        Data data1 = new Data();
        data1.put("abc", 123);
        data1.put("xyz", "test");
        Data data2 = new Data();
        data2.put("abc", 123);
        data2.put("xyz", "test");
        NodeData nodeData1 = new NodeData(id, taskId, taskName, timeStamp, data1);
        NodeData nodeData2 = new NodeData(id, taskId, taskName, timeStamp, data2);
        assertArrayEquals(srpService.buildHash(nodeData1), srpService.buildHash(nodeData2));
    }

    @Test
    public void testGetHashDifferentTaskName() {
        UUID id = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        String taskName = "testTask";
        long timeStamp = 0;
        Data data = new Data();
        NodeData nodeData1 = new NodeData(id, taskId, taskName, timeStamp, data);
        NodeData nodeData2 = new NodeData(id, taskId, taskName + "test", timeStamp, data);
        assertFalse(Arrays.equals(srpService.buildHash(nodeData1), srpService.buildHash(nodeData2)));
    }

    @Test
    public void testGetHashDifferentId() {
        UUID taskId = UUID.randomUUID();
        String taskName = "testTask";
        long timeStamp = 0;
        Data data = new Data();
        NodeData nodeData1 = new NodeData(UUID.randomUUID(), taskId, taskName, timeStamp, data);
        NodeData nodeData2 = new NodeData(UUID.randomUUID(), taskId, taskName, timeStamp, data);
        assertFalse(Arrays.equals(srpService.buildHash(nodeData1), srpService.buildHash(nodeData2)));
    }

    @Test
    public void testGetHashDifferentStatus() {
        UUID taskId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        String taskName = "testTask";
        String message = "abc";
        Data data = new Data();
        NodeResponse nodeData1 = new NodeResponse(id, taskId, 0, ResponseStatus.NORMAL, message, data, taskName, 0);
        NodeResponse nodeData2 = new NodeResponse(id, taskId, 0, ResponseStatus.FAILED, message, data, taskName, 0);
        assertFalse(Arrays.equals(srpService.buildHash(nodeData1), srpService.buildHash(nodeData2)));
    }

    @Test
    public void testGetHashSameDataResponse() {
        UUID taskId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        String taskName = "testTask";
        String message = "abc";
        Data data = new Data();
        NodeResponse nodeData1 = new NodeResponse(id, taskId, 0, ResponseStatus.NORMAL, message, data, taskName, 0);
        NodeResponse nodeData2 = new NodeResponse(id, taskId, 0, ResponseStatus.NORMAL, message, data, taskName, 0);
        assertArrayEquals(srpService.buildHash(nodeData1), srpService.buildHash(nodeData2));
    }
}

