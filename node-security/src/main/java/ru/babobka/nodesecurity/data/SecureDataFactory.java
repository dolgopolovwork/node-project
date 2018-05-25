package ru.babobka.nodesecurity.data;

import ru.babobka.nodesecurity.service.SRPService;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeutils.container.Container;

/**
 * Created by 123 on 25.04.2018.
 */
public class SecureDataFactory {
    private final SRPService SRPService = Container.getInstance().get(SRPService.class);

    public SecureNodeRequest create(NodeRequest request, byte[] secretKey) {
        return new SecureNodeRequest(request, SRPService.buildMac(request, secretKey));
    }

    public SecureNodeResponse create(NodeResponse response, byte[] secretKey) {
        return new SecureNodeResponse(response, SRPService.buildMac(response, secretKey));
    }
}
