package ru.babobka.submaster;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import ru.babobka.masternoderun.MasterApp;
import ru.babobka.nodeclient.console.CLI;
import ru.babobka.nodeconfigs.ConfigsApplicationContainer;
import ru.babobka.nodeconfigs.exception.EnvConfigCreationException;
import ru.babobka.nodeconfigs.master.validation.MasterServerConfigValidator;
import ru.babobka.nodeconfigs.service.ConfigProvider;
import ru.babobka.nodeconfigs.slave.validation.SlaveServerConfigValidator;
import ru.babobka.nodesecurity.SecurityApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.time.TimerInvoker;
import ru.babobka.nodeutils.util.StreamUtil;
import ru.babobka.nodeutils.waiter.Waiter;
import ru.babobka.slavenoderun.SlaveApp;
import ru.babobka.slavenoderun.factory.MasterBackedSlaveServerFactory;
import ru.babobka.slavenoderun.waiter.SlaveCreationAbilityWaiter;
import ru.babobka.submaster.listener.GotSlavesListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static ru.babobka.nodeutils.key.SlaveServerKey.SLAVE_CREATION_WAITER;

public class SubMasterApp extends CLI {

    private static final Waiter slaveCreationWaiter = new SlaveCreationAbilityWaiter();

    static {
        Container.getInstance().put(container -> {
            container.put(new ConfigProvider());
            container.put(new StreamUtil());
            container.put(new SlaveServerConfigValidator());
            container.put(new MasterBackedSlaveServerFactory());
            container.put(TimerInvoker.createMaxOneSecondDelay());
            container.put(new MasterServerConfigValidator());
            container.put(new SecurityApplicationContainer());
            container.put(new ConfigsApplicationContainer());
            container.put(new GotSlavesListener(slaveCreationWaiter));
            container.put(SLAVE_CREATION_WAITER, slaveCreationWaiter);
        });
    }

    private final SlaveApp slaveApp = new SlaveApp();
    private final MasterApp masterApp = new MasterApp();

    public static void main(String[] args) {
        new SubMasterApp().onStart(args);
    }

    @Override
    public List<Option> createOptions() {
        List<Option> options = new ArrayList<>();
        options.addAll(masterApp.createOptions());
        options.addAll(slaveApp.createOptions());
        return options;
    }

    @Override
    public void run(CommandLine cmd) throws IOException, EnvConfigCreationException {
        masterApp.run(cmd);
        slaveApp.run(cmd);
    }

    @Override
    public String getAppName() {
        return "sub-master-node";
    }
}
