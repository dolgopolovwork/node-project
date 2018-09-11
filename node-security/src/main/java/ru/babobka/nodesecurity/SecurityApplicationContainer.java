package ru.babobka.nodesecurity;

import ru.babobka.nodesecurity.data.SecureDataFactory;
import ru.babobka.nodesecurity.service.*;
import ru.babobka.nodeutils.container.AbstractApplicationContainer;
import ru.babobka.nodeutils.container.Container;

/**
 * Created by 123 on 25.04.2018.
 */
public class SecurityApplicationContainer extends AbstractApplicationContainer {
    @Override
    protected void containImpl(Container container) {
        container.put(new SRPService());
        container.put(new SecureDataFactory());
        container.put(new RSAService());
        container.put(new AESService());
        container.put(new SecureJSONService());
        container.put(new SecureConfigService());
        container.put(new ConfigProvider());
    }
}
