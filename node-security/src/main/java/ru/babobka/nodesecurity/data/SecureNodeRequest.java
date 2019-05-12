package ru.babobka.nodesecurity.data;

import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeutils.util.ArrayUtil;

import java.util.Arrays;

/**
 * Created by 123 on 24.04.2018.
 */
public class SecureNodeRequest extends NodeRequest {
    private static final long serialVersionUID = 5027409098381331691L;
    private final byte[] signature;

    public SecureNodeRequest(NodeRequest request, byte[] signature) {
        super(request.getId(),
                request.getTaskId(),
                request.getTaskName(),
                request.getData(),
                request.getRequestStatus(),
                request.getTimeStamp());
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

        SecureNodeRequest request = (SecureNodeRequest) o;

        return Arrays.equals(signature, request.signature);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Arrays.hashCode(signature);
        return result;
    }
}
