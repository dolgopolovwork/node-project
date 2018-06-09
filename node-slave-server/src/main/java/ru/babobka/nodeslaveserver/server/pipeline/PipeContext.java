package ru.babobka.nodeslaveserver.server.pipeline;

import lombok.NonNull;
import ru.babobka.nodesecurity.auth.AuthCredentials;
import ru.babobka.nodesecurity.auth.AuthResult;
import ru.babobka.nodeutils.network.NodeConnection;
import ru.babobka.nodeutils.time.ServerTime;

/**
 * Created by 123 on 08.06.2018.
 */
public class PipeContext {

    private final NodeConnection connection;
    private final AuthCredentials credentials;
    private ServerTime serverTime;
    private AuthResult authResult;

    public PipeContext(@NonNull NodeConnection connection,
                       @NonNull AuthCredentials credentials) {
        this.connection = connection;
        this.credentials = credentials;
    }

    public NodeConnection getConnection() {
        return connection;
    }

    public AuthResult getAuthResult() {
        return authResult;
    }

    public void setAuthResult(AuthResult authResult) {
        this.authResult = authResult;
    }

    public AuthCredentials getCredentials() {
        return credentials;
    }

    public ServerTime getServerTime() {
        return serverTime;
    }

    public void setServerTime(ServerTime serverTime) {
        this.serverTime = serverTime;
    }
}
