package ru.babobka.nodeconfigs.cli.master;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import ru.babobka.nodeclient.console.CLI;
import ru.babobka.nodeconfigs.ConfigsApplicationContainer;
import ru.babobka.nodeconfigs.enums.ConfExt;
import ru.babobka.nodeconfigs.master.MasterServerConfig;
import ru.babobka.nodeconfigs.service.ConfigProvider;
import ru.babobka.nodesecurity.SecurityApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.time.TimerInvoker;
import ru.babobka.nodeutils.util.StreamUtil;
import ru.babobka.nodeutils.util.TextUtil;

import java.io.File;

/**
 * Created by 123 on 13.09.2018.
 */
public class MasterConfigReaderMain extends CLI {

    private static final String CONFIG_PATH_OPTION = "configPath";

    static {
        Container.getInstance().put(container -> {
            container.put(new StreamUtil());
            container.put(TimerInvoker.createMaxOneSecondDelay());
            container.put(new SecurityApplicationContainer());
            container.put(new ConfigsApplicationContainer());
        });
    }

    private final ConfigProvider configProvider = Container.getInstance().get(ConfigProvider.class);

    @Override
    protected Options createOptions() {
        Options options = new Options();
        Option configPath = Option.builder().longOpt(CONFIG_PATH_OPTION).hasArg().
                desc("Path to master node configuration file").required().build();
        options.addOption(configPath);
        return options;
    }

    @Override
    protected void extraValidation(CommandLine cmd) throws ParseException {
        String configPath = cmd.getOptionValue(CONFIG_PATH_OPTION);
        if (TextUtil.isEmpty(configPath)) {
            throw new IllegalArgumentException("path to configuration file must be specified");
        } else if (!(configPath.endsWith(ConfExt.ENCRYPTED.extension) || configPath.endsWith(ConfExt.JSON.extension))) {
            throw new IllegalArgumentException("bad configuration file extension");
        }
        File configFile = new File(configPath);
        if (!configFile.exists() || !configFile.isFile()) {
            throw new IllegalArgumentException("file '" + configPath + "' doesn't exists");
        }
    }

    @Override
    protected void run(CommandLine cmd) throws Exception {
        String configPath = cmd.getOptionValue(CONFIG_PATH_OPTION);
        String password = null;
        if (configPath.endsWith(ConfExt.ENCRYPTED.extension)) {
            //TODO this is not safe. This line is visible in console
            password = readLine("Please enter password to decrypt configuration");
        }
        MasterServerConfig config = configProvider.getConfig(configPath, MasterServerConfig.class, password);
        print(config.toString());

    }

    @Override
    public String getAppName() {
        return "master-config-reader";
    }
}
