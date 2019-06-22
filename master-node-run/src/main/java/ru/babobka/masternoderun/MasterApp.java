package ru.babobka.masternoderun;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.log4j.Logger;
import ru.babobka.nodeclient.console.CLI;
import ru.babobka.nodeconfigs.ConfigsApplicationContainer;
import ru.babobka.nodeconfigs.exception.EnvConfigCreationException;
import ru.babobka.nodeconfigs.master.validation.MasterServerConfigValidator;
import ru.babobka.nodesecurity.SecurityApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.enums.Env;
import ru.babobka.nodeutils.time.TimerInvoker;
import ru.babobka.nodeutils.util.StreamUtil;
import ru.babobka.nodeutils.util.TextUtil;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Created by 123 on 06.12.2017.
 */
public class MasterApp extends CLI {

    private static Logger logger = Logger.getLogger(MasterApp.class);

    private static void init() {
        Container.getInstance().put(container -> {
            container.put(TimerInvoker.createMaxOneSecondDelay());
            container.put(new StreamUtil());
            container.put(new MasterServerConfigValidator());
            container.put(new SecurityApplicationContainer());
            container.put(new ConfigsApplicationContainer());
        });
    }

    @Override
    public List<Option> createOptions() {
        return Collections.emptyList();
    }

    @Override
    public void run(CommandLine cmd) throws IOException, EnvConfigCreationException {
        MasterServerRunner masterServerRunner = new MasterServerRunner();
        masterServerRunner.run();
    }

    @Override
    public String getAppName() {
        return "master-node-run";
    }

    public static void main(String[] args) {
        init();
        new MasterApp().onStart(args);
    }
}
