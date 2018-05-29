package ru.babobka.vsjws.model.http.session;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Map;

/**
 * Created by 123 on 27.05.2018.
 */
public class Session {
    private final InetAddress address;
    private final Map<String, Serializable> data;

    public Session(InetAddress address, Map<String, Serializable> data) {
        if (address == null) {
            throw new IllegalArgumentException("address is null");
        } else if (data == null) {
            throw new IllegalArgumentException("data is null");
        }
        this.address = address;
        this.data = data;
    }

    public InetAddress getAddress() {
        return address;
    }

    public Map<String, Serializable> getData() {
        return data;
    }
}