package ru.babobka.nodemasterserver.slave.pipeline;

import lombok.NonNull;
import ru.babobka.nodesecurity.auth.AuthResult;
import ru.babobka.nodeutils.network.NodeConnection;

import java.util.Set;

/**
 * Created by 123 on 08.06.2018.
 */
public class PipeContext {

    private final NodeConnection connection;
    private Set<String> availableTasks;
    private AuthResult authResult;

    public PipeContext(@NonNull NodeConnection connection) {
        this.connection = connection;
    }

    public NodeConnection getConnection() {
        return connection;
    }

    public Set<String> getAvailableTasks() {
        return availableTasks;
    }

    public void setAvailableTasks(Set<String> availableTasks) {
        this.availableTasks = availableTasks;
    }

    public AuthResult getAuthResult() {
        return authResult;
    }

    public void setAuthResult(AuthResult authResult) {
        this.authResult = authResult;
    }
}
