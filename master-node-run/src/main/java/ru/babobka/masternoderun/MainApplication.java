package ru.babobka.masternoderun;

import com.google.gson.Gson;
import ru.babobka.nodemasterserver.validation.config.MasterServerConfigValidator;
import ru.babobka.nodeutils.container.ApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.util.StreamUtil;

import java.io.IOException;

/**
 * Created by 123 on 06.12.2017.
 */
public class MainApplication {

    static {
        new ApplicationContainer() {
            @Override
            public void contain(Container container) {
                container.put(new Gson());
                container.put(new StreamUtil());
                container.put(new ConfigFactory());
                container.put(new MasterServerConfigValidator());
            }
        }.contain(Container.getInstance());
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            printErr("Path to config was not set");
            return;
        }
        String pathToConfig = args[0];
        try {
            MasterServerRunner masterServerRunner = new MasterServerRunner();
            masterServerRunner.run(pathToConfig);
        } catch (Exception e) {
            printErr("Error occurred while startup");
            e.printStackTrace();
        }
    }

    private static void printErr(String msg) {
        System.err.println(msg);
    }

}
