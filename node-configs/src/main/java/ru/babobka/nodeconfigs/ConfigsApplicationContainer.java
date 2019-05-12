package ru.babobka.nodeconfigs;

import ru.babobka.nodeconfigs.service.ConfigProvider;
import ru.babobka.nodeutils.container.AbstractApplicationContainer;
import ru.babobka.nodeutils.container.Container;

/**
 * Created by 123 on 25.04.2018.
 */
public class ConfigsApplicationContainer extends AbstractApplicationContainer {
    @Override
    protected void containImpl(Container container) {
        container.put(new ConfigProvider());
    }
}
