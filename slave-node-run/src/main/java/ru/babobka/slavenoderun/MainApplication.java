package ru.babobka.slavenoderun;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import ru.babobka.nodeclient.console.CLI;
import ru.babobka.nodeconfigs.slave.validation.SlaveServerConfigValidator;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.enums.Env;
import ru.babobka.nodeutils.util.StreamUtil;
import ru.babobka.nodeutils.util.TextUtil;

/**
 * Created by 123 on 06.12.2017.
 */
public class MainApplication extends CLI {
    private static final String CONFIG_PATH_OPTION = "configPath";
    private static final String LOGIN_OPTION = "login";
    private static final String PASSWORD_OPTION = "password";
    private static final String CONFIG_PATH_OPT = "c";
    private static final String LOGIN_OPT = "l";
    private static final String PASSWORD_OPT = "p";

    static {
        Container.getInstance().put(new StreamUtil());
        Container.getInstance().put(new SlaveServerConfigValidator());
        Container.getInstance().put(new SlaveServerFactory());
    }

    @Override
    protected Options createOptions() {
        Options options = new Options();
        Option configPath = Option.builder(CONFIG_PATH_OPT).longOpt(CONFIG_PATH_OPTION).hasArg().
                desc("Defines path to configuration json file. May be omitted, if environment variable " + Env.NODE_SLAVE_CONFIG + " is set.").build();
        Option loginOption = Option.builder(LOGIN_OPT).longOpt(LOGIN_OPTION).hasArg().
                desc("Slave login").required().build();
        Option passwordOption = Option.builder(PASSWORD_OPT).longOpt(PASSWORD_OPTION).hasArg().
                desc("Slave password").required().build();
        options.addOption(configPath);
        options.addOption(loginOption);
        options.addOption(passwordOption);
        return options;
    }

    @Override
    protected void run(CommandLine cmd) {
        String login = cmd.getOptionValue(LOGIN_OPTION);
        String password = cmd.getOptionValue(PASSWORD_OPTION);
        String pathToConfig = getPathToConfig(cmd);
        new ConnectionSafeSlaveRunner().run(pathToConfig, login, password);
    }

    @Override
    public String getAppName() {
        return "slave-node-run";
    }

    private static String getPathToConfig(CommandLine cmd) {
        String configPath = cmd.getOptionValue(CONFIG_PATH_OPTION);
        if (configPath != null) {
            return configPath;
        }
        configPath = TextUtil.getEnv(Env.NODE_SLAVE_CONFIG);
        if (configPath != null) {
            print("Path to config was taken from environment variable " + Env.NODE_SLAVE_CONFIG);
            return configPath;
        }
        throw new IllegalArgumentException("Path to config was not set");
    }

    public static void main(String[] args) {
        new MainApplication().onMain(args);
    }
}
