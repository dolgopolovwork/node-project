package ru.babobka.nodeconfigs.cli.master;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import ru.babobka.nodeclient.console.CLI;
import ru.babobka.nodeconfigs.ConfigsApplicationContainer;
import ru.babobka.nodeconfigs.enums.ConfExt;
import ru.babobka.nodeconfigs.master.MasterServerConfig;
import ru.babobka.nodeconfigs.master.validation.MasterServerConfigValidator;
import ru.babobka.nodeconfigs.service.ConfigProvider;
import ru.babobka.nodesecurity.SecurityApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.time.TimerInvoker;
import ru.babobka.nodeutils.util.StreamUtil;

import java.io.File;
import java.io.IOException;

/**
 * Created by 123 on 17.09.2018.
 */
public class MasterConfigEncryptionMain extends CLI {

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
        return options.addOption(configPath);
    }

    @Override
    protected void extraValidation(CommandLine cmd) throws ParseException {
        String configPath = cmd.getOptionValue(CONFIG_PATH_OPTION);
        File configFile = new File(configPath);
        if (!configFile.exists()) {
            throw new IllegalArgumentException("There is no configuration file located at '" + configPath + "'");
        } else if (!configPath.endsWith(ConfExt.JSON.extension)) {
            throw new IllegalArgumentException("Invalid configuration file extension");
        }
        try {
            MasterServerConfig masterServerConfig = getMasterServerConfig(configPath);
            MasterServerConfigValidator validator = new MasterServerConfigValidator();
            validator.validate(masterServerConfig);
        } catch (IOException | IllegalArgumentException e) {
            throw new IllegalStateException("Cannot read master server config", e);
        }
    }

    @Override
    protected void run(CommandLine cmd) throws Exception {
        String password = readPassword("please provide password to encrypt configuration");
        String configPath = cmd.getOptionValue(CONFIG_PATH_OPTION);
        File originalConfigFile = new File(configPath);
        String configFileFolder = originalConfigFile.getParent();
        configProvider.createConfig(configFileFolder, getMasterServerConfig(configPath), password);
        print("Config file was successfully encrypted. Generated file is located at '" + configFileFolder + "'");
    }

    @Override
    public String getAppName() {
        return "master-config-encryption";
    }

    private MasterServerConfig getMasterServerConfig(String configPath) throws IOException {
        return configProvider.getConfig(configPath, MasterServerConfig.class, null);
    }
}
