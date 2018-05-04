package ru.babobka.nodesecurity.data;

import ru.babobka.nodesecurity.service.SecurityService;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeutils.container.Container;

/**
 * Created by 123 on 25.04.2018.
 */
public class SecureDataFactory {
    private final SecurityService securityService = Container.getInstance().get(SecurityService.class);

    public SecureNodeRequest create(NodeRequest request, byte[] secretKey) {
        return new SecureNodeRequest(request, securityService.buildMac(request, secretKey));
    }

    public SecureNodeResponse create(NodeResponse response, byte[] secretKey) {
        return new SecureNodeResponse(response, securityService.buildMac(response, secretKey));
    }
}
