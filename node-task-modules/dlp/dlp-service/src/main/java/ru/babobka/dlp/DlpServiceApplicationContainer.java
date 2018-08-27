package ru.babobka.dlp;

import ru.babobka.dlp.service.PollardDlpServiceFactory;
import ru.babobka.dlp.service.pollard.ClassicPollardDlpService;
import ru.babobka.dlp.service.pollard.PollardCollisionService;
import ru.babobka.dlp.service.pollard.parallel.PrimeDistinguishable;
import ru.babobka.nodeutils.container.AbstractApplicationContainer;
import ru.babobka.nodeutils.container.Container;

/**
 * Created by 123 on 29.10.2017.
 */
public class DlpServiceApplicationContainer extends AbstractApplicationContainer {

    @Override
    protected void containImpl(Container container) {
        container.put(new PollardCollisionService());
        container.put(new ClassicPollardDlpService());
        container.put(new PrimeDistinguishable());
        container.put(new PollardDlpServiceFactory());
    }
}
