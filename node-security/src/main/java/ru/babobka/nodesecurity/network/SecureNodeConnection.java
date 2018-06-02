package ru.babobka.nodesecurity.network;

import ru.babobka.nodesecurity.data.SecureDataFactory;
import ru.babobka.nodesecurity.exception.NodeSecurityException;
import ru.babobka.nodesecurity.service.SRPService;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.enumerations.RequestStatus;
import ru.babobka.nodeserials.enumerations.ResponseStatus;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.network.NodeConnection;
import ru.babobka.nodeutils.util.ArrayUtil;

import java.io.IOException;

/**
 * Created by 123 on 24.04.2018.
 */
public class SecureNodeConnection implements NodeConnection {

    private final SecureDataFactory secureDataFactory = Container.getInstance().get(SecureDataFactory.class);
    private final SRPService SRPService = Container.getInstance().get(SRPService.class);
    private final byte[] secretKey;
    private final NodeConnection connection;

    public SecureNodeConnection(NodeConnection nodeConnection, byte[] secretKey) {
        if (nodeConnection == null) {
            throw new IllegalArgumentException("nodeConnection is null");
        } else if (nodeConnection.isClosed()) {
            throw new IllegalArgumentException("nodeConnection is closed");
        } else if (ArrayUtil.isEmpty(secretKey)) {
            throw new IllegalArgumentException("secretKey was not set");
        }
        this.secretKey = secretKey.clone();
        this.connection = nodeConnection;
    }

    @Override
    public void setReadTimeOut(int timeOutMillis) throws IOException {
        connection.setReadTimeOut(timeOutMillis);
    }

    @Override
    public <T> T receive() throws IOException {
        T object = connection.receive();
        if (SRPService.isSecure(object, secretKey)) {
            return object;
        }
        close();
        throw new NodeSecurityException("can not receive insecure instance of class " + object.getClass().getCanonicalName());
    }

    @Override
    public void send(Object object) throws IOException {
        if (object == null) {
            throw new IllegalArgumentException("can not send null object");
        } else if (object instanceof NodeRequest) {
            send((NodeRequest) object);
            return;
        } else if (object instanceof NodeResponse) {
            send((NodeResponse) object);
            return;
        }
        throw new NodeSecurityException("can not send insecure instance of class " + object.getClass().getCanonicalName());
    }

    private void send(NodeRequest request) throws IOException {
        if (request.getRequestStatus() != RequestStatus.HEART_BEAT) {
            connection.send(secureDataFactory.create(request, secretKey));
        } else {
            connection.send(request);
        }
    }

    private void send(NodeResponse response) throws IOException {
        if (response.getStatus() != ResponseStatus.HEART_BEAT) {
            connection.send(secureDataFactory.create(response, secretKey));
        } else {
            connection.send(response);
        }
    }

    @Override
    public void close() {
        connection.close();
    }

    @Override
    public boolean isClosed() {
        return connection.isClosed();
    }

    @Override
    public String toString() {
        return connection.toString();
    }
}
