package ru.babobka.nodesecurity.sign;

import lombok.NonNull;
import ru.babobka.nodesecurity.data.SecureNodeRequest;
import ru.babobka.nodesecurity.data.SecureNodeResponse;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;

import java.io.IOException;

public interface Signer {

    SecureNodeRequest sign(@NonNull NodeRequest request) throws IOException;

    SecureNodeResponse sign(@NonNull NodeResponse response) throws IOException;

}
