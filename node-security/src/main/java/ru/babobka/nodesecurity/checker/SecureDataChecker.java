package ru.babobka.nodesecurity.checker;

import lombok.NonNull;
import ru.babobka.nodesecurity.data.SecureNodeRequest;
import ru.babobka.nodesecurity.data.SecureNodeResponse;
import ru.babobka.nodesecurity.sign.SignatureValidator;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.enumerations.RequestStatus;
import ru.babobka.nodeserials.enumerations.ResponseStatus;
import ru.babobka.nodeutils.container.Container;

import java.io.IOException;
import java.security.PublicKey;

public class SecureDataChecker {

    private final SignatureValidator signatureValidator = Container.getInstance().get(SignatureValidator.class);

    public boolean isSecure(@NonNull Object object, @NonNull PublicKey publicKey) throws IOException {
        if (object instanceof NodeResponse) {
            return isSecure((NodeResponse) object, publicKey);
        } else if (object instanceof NodeRequest) {
            return isSecure((NodeRequest) object, publicKey);
        }
        return false;
    }

    private boolean isSecure(NodeResponse response, PublicKey publicKey) throws IOException {
        if (response.getStatus() == ResponseStatus.HEART_BEAT) {
            return true;
        } else if (!(response instanceof SecureNodeResponse)) {
            return false;
        }
        return signatureValidator.isValid((SecureNodeResponse) response, publicKey);
    }

    private boolean isSecure(NodeRequest request, PublicKey publicKey) throws IOException {
        if (request.getRequestStatus() == RequestStatus.HEART_BEAT) {
            return true;
        } else if (!(request instanceof SecureNodeRequest)) {
            return false;
        }
        return signatureValidator.isValid((SecureNodeRequest) request, publicKey);
    }

}
