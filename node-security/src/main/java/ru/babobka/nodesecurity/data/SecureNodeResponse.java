package ru.babobka.nodesecurity.data;

import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeutils.util.ArrayUtil;

import java.util.Arrays;

/**
 * Created by 123 on 24.04.2018.
 */
public class SecureNodeResponse extends NodeResponse {
    private static final long serialVersionUID = -1606074489575029102L;
    private final byte[] signature;

    public SecureNodeResponse(NodeResponse response, byte[] signature) {
        super(response.getId(),
                response.getTaskId(),
                response.getTimeTakes(),
                response.getStatus(),
                response.getMessage(),
                response.getData(),
                response.getTaskName(),
                response.getTimeStamp());
        if (ArrayUtil.isEmpty(signature)) {
            throw new IllegalArgumentException("signature was not set");
        }
        this.signature = signature.clone();
    }

    public byte[] getSignature() {
        return signature.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        SecureNodeResponse response = (SecureNodeResponse) o;

        return Arrays.equals(signature, response.signature);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Arrays.hashCode(signature);
        return result;
    }
}
