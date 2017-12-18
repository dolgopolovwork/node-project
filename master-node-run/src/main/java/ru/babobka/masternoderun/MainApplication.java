package ru.babobka.masternoderun;

import ru.babobka.nodemasterserver.validation.config.MasterServerConfigValidator;
import ru.babobka.nodeutils.container.ApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.util.StreamUtil;

import java.io.IOException;

/**
 * Created by 123 on 06.12.2017.
 */
public class MainApplication {

    private static final String ENV_VAR_CONFIG = "NODE_MASTER_CONFIG";

    static {
        new ApplicationContainer() {
            @Override
            public void contain(Container container) {
                container.put(new StreamUtil());
                container.put(new MasterServerConfigValidator());
            }
        }.contain(Container.getInstance());
    }

    public static void main(String[] args) throws IOException {
        try {
            String pathToConfig = getPathToConfig(args);
            MasterServerRunner masterServerRunner = new MasterServerRunner();
            masterServerRunner.run(pathToConfig);
        } catch (Exception e) {
            printErr("Error occurred while startup. " + e.getMessage());
        }
    }

    private static void printErr(String msg) {
        System.err.println(msg);
    }

    private static void print(String msg) {
        System.out.println(msg);
    }

    private static String getPathToConfig(String[] args) {
        if (args.length < 1) {
            String pathToConfig = System.getenv(ENV_VAR_CONFIG);
            if (pathToConfig != null) {
                print("Path to config was taken from environment variable " + ENV_VAR_CONFIG);
                return pathToConfig;
            }
            throw new IllegalArgumentException("Path to config was not set");
        }
        return args[0];
    }

}
