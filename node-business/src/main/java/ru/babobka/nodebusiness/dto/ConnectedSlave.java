package ru.babobka.nodebusiness.dto;

import lombok.NonNull;

import java.net.InetAddress;
import java.util.UUID;

public class ConnectedSlave {

    private final UUID id;
    private final InetAddress ipAddress;
    private final String userName;

    public ConnectedSlave(@NonNull UUID id, @NonNull InetAddress ipAddress, @NonNull String userName) {
        this.id = id;
        this.ipAddress = ipAddress;
        this.userName = userName;
    }

    public UUID getId() {
        return id;
    }

    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public String getUserName() {
        return userName;
    }
}
