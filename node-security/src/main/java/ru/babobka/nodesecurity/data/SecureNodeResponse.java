package ru.babobka.nodesecurity.data;

import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeutils.util.ArrayUtil;

import java.util.Arrays;

/**
 * Created by 123 on 24.04.2018.
 */
public class SecureNodeResponse extends NodeResponse {
    private static final long serialVersionUID = -1606074489575029102L;
    private final byte[] mac;

    SecureNodeResponse(NodeResponse response, byte[] mac) {
        super(response.getId(),
                response.getTaskId(),
                response.getTimeTakes(),
                response.getStatus(),
                response.getMessage(),
                response.getData(),
                response.getTaskName(),
                response.getTimeStamp());
        if (ArrayUtil.isEmpty(mac)) {
            throw new IllegalArgumentException("mac was not set");
        }
        this.mac = mac.clone();
    }

    public byte[] getMac() {
        return mac.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        SecureNodeResponse response = (SecureNodeResponse) o;

        return Arrays.equals(mac, response.mac);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Arrays.hashCode(mac);
        return result;
    }
}
