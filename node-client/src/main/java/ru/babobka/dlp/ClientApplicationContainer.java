package ru.babobka.dlp;

import ru.babobka.nodeutils.container.ApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.util.StreamUtil;

/**
 * Created by 123 on 16.12.2017.
 */
public class ClientApplicationContainer implements ApplicationContainer {

    @Override
    public void contain(Container container) {
        container.put(new StreamUtil());
    }
}
