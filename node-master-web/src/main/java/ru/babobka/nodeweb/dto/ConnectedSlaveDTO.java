package ru.babobka.nodeweb.dto;

import lombok.NonNull;

import java.io.Serializable;

public class ConnectedSlaveDTO implements Serializable {
    private static final long serialVersionUID = 8763191270343475569L;
    private final String address;
    private final String id;
    private final String userName;

    public ConnectedSlaveDTO(@NonNull String address, @NonNull String id, @NonNull String userName) {
        this.address = address;
        this.id = id;
        this.userName = userName;
    }

    public String getAddress() {
        return address;
    }

    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }
}
