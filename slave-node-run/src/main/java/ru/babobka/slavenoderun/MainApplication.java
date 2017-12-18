package ru.babobka.slavenoderun;

import ru.babobka.nodeslaveserver.validator.config.SlaveServerConfigValidator;
import ru.babobka.nodeutils.container.ApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.util.StreamUtil;

import java.io.IOException;

/**
 * Created by 123 on 06.12.2017.
 */
public class MainApplication {

    private static final String ENV_VAR_CONFIG = "NODE_SLAVE_CONFIG";
    private static final String COMMAND_LINE_WARNING = "You must specify at least 2 arguments: login and sha2 hashed password";

    static {
        new ApplicationContainer() {
            @Override
            public void contain(Container container) {
                container.put(new StreamUtil());
                container.put(new SlaveServerConfigValidator());
                container.put(new SlaveServerFactory());
            }
        }.contain(Container.getInstance());
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            printErr(COMMAND_LINE_WARNING);
            return;
        }
        try {
            String login = args[0];
            String password = args[1];
            String pathToConfig = getPathToConfig(args);
            new ConnectionSafeSlaveRunner().run(pathToConfig, login, password);
        } catch (Exception e) {
            printErr(e.getMessage());
        }
    }

    private static String getPathToConfig(String[] args) {
        if (args.length < 3) {
            String pathToConfig = System.getenv(ENV_VAR_CONFIG);
            if (pathToConfig != null) {
                print("Path to config was taken from environment variable " + ENV_VAR_CONFIG);
                return pathToConfig;
            }
            throw new IllegalArgumentException("Path to config was not set");
        }
        return args[2];
    }


    private static void printErr(String msg) {
        System.err.println(msg);
    }

    private static void print(String msg) {
        System.out.println(msg);
    }


}
