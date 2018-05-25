package ru.babobka.nodesecurity;

import ru.babobka.nodesecurity.data.SecureDataFactory;
import ru.babobka.nodesecurity.service.RSAService;
import ru.babobka.nodesecurity.service.SRPService;
import ru.babobka.nodeutils.container.ApplicationContainer;
import ru.babobka.nodeutils.container.Container;

/**
 * Created by 123 on 25.04.2018.
 */
public class SecurityApplicationContainer implements ApplicationContainer {
    @Override
    public void contain(Container container) {
        container.put(new SRPService());
        container.put(new SecureDataFactory());
        container.put(new RSAService());
    }
}
