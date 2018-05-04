package ru.babobka.nodesecurity.data;

import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeutils.util.ArrayUtil;

import java.util.Arrays;

/**
 * Created by 123 on 24.04.2018.
 */
public class SecureNodeRequest extends NodeRequest {
    private static final long serialVersionUID = 5027409098381331691L;
    private final byte[] mac;

    SecureNodeRequest(NodeRequest request, byte[] mac) {
        super(request.getId(),
                request.getTaskId(),
                request.getTaskName(),
                request.getData(),
                request.getRequestStatus(),
                request.getTimeStamp());
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

        SecureNodeRequest request = (SecureNodeRequest) o;

        return Arrays.equals(mac, request.mac);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Arrays.hashCode(mac);
        return result;
    }
}
