package ru.babobka.slavenoderun;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import ru.babobka.nodeclient.console.CLI;
import ru.babobka.nodeconfigs.ConfigsApplicationContainer;
import ru.babobka.nodeconfigs.slave.validation.SlaveServerConfigValidator;
import ru.babobka.nodeutils.key.SlaveServerKey;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.util.StreamUtil;
import ru.babobka.slavenoderun.factory.PlainSlaveServerFactory;
import ru.babobka.slavenoderun.waiter.DummyWaiter;

import java.util.Collections;
import java.util.List;

/**
 * Created by 123 on 06.12.2017.
 */
public class SlaveApp extends CLI {

    private static void init() {
        Container.getInstance().put(container -> {
            container.put(new StreamUtil());
            container.put(new SlaveServerConfigValidator());
            container.put(new ConfigsApplicationContainer());
            container.put(new PlainSlaveServerFactory());
            container.put(SlaveServerKey.SLAVE_CREATION_WAITER, new DummyWaiter());
        });
    }

    @Override
    public List<Option> createOptions() {
        return Collections.emptyList();
    }

    @Override
    public void run(CommandLine cmd) {
        new SlaveRunner().run();
    }

    @Override
    public String getAppName() {
        return "slave-node-run";
    }


    public static void main(String[] args) {
        init();
        new SlaveApp().onStart(args);
    }
}
