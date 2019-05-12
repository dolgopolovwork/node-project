package ru.babobka.nodesecurity.network;

import lombok.NonNull;
import org.apache.log4j.Logger;
import ru.babobka.nodesecurity.checker.SecureDataChecker;
import ru.babobka.nodesecurity.exception.NodeSecurityException;
import ru.babobka.nodesecurity.sign.DigitalSigner;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.enumerations.RequestStatus;
import ru.babobka.nodeserials.enumerations.ResponseStatus;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Created by 123 on 24.04.2018.
 */
public class SecureNodeConnection implements NodeConnection {

    private static final Logger logger = Logger.getLogger(SecureNodeConnection.class);
    private final DigitalSigner digitalSigner = Container.getInstance().get(DigitalSigner.class);
    private final SecureDataChecker dataChecker = Container.getInstance().get(SecureDataChecker.class);
    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private final NodeConnection connection;

    public SecureNodeConnection(@NonNull NodeConnection nodeConnection,
                                @NonNull PrivateKey privateKey,
                                @NonNull PublicKey publicKey) {
        if (nodeConnection.isClosed()) {
            throw new IllegalArgumentException("nodeConnection is closed");
        }
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.connection = nodeConnection;
    }

    @Override
    public void setReadTimeOut(int timeOutMillis) throws IOException {
        connection.setReadTimeOut(timeOutMillis);
    }

    @Override
    public <T> T receive() throws IOException {
        try {
            return receiveNoClose();
        } catch (NodeSecurityException e) {
            close();
            throw e;
        }
    }

    public <T> T receiveNoClose() throws IOException {
        T object = connection.receive();
        if (dataChecker.isSecure(object, publicKey)) {
            return object;
        }
        throw new NodeSecurityException("can not receive insecure instance of class " + object.getClass().getCanonicalName());
    }

    @Override
    public void send(@NonNull Object object) throws IOException {

        if (object instanceof NodeRequest) {
            send((NodeRequest) object);
            return;
        } else if (object instanceof NodeResponse) {
            send((NodeResponse) object);
            return;
        }
        throw new NodeSecurityException("can not send insecure instance of class " + object.getClass().getCanonicalName());
    }

    @Override
    public void sendIgnoreException(Object object) {
        try {
            send(object);
        } catch (Exception e) {
            logger.error("cannot send", e);
        }
    }

    @Override
    public void sendThrowRuntime(Object object) {
        try {
            send(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void send(NodeRequest request) throws IOException {
        if (request.getRequestStatus() != RequestStatus.HEART_BEAT) {
            connection.send(digitalSigner.sign(request, privateKey));
        } else {
            connection.send(request);
        }
    }

    protected void send(NodeResponse response) throws IOException {
        if (response.getStatus() != ResponseStatus.HEART_BEAT) {
            connection.send(digitalSigner.sign(response, privateKey));
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
