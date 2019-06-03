package ru.babobka.nodesecurity;

import ru.babobka.nodesecurity.checker.SecureDataChecker;
import ru.babobka.nodesecurity.sign.DigitalSigner;
import ru.babobka.nodeutils.container.AbstractApplicationContainer;
import ru.babobka.nodeutils.container.Container;

/**
 * Created by 123 on 25.04.2018.
 */
public class SecurityApplicationContainer extends AbstractApplicationContainer {
    @Override
    protected void containImpl(Container container) {
        container.put(new DigitalSigner());
        container.put(new SecureDataChecker());
    }
}
