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

    static {
        new ApplicationContainer() {
            @Override
            public void contain(Container container) {
                container.put(new StreamUtil());
                container.put(new SlaveServerConfigValidator());
            }
        }.contain(Container.getInstance());
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            printErr("Invalid command. You must specify 3 arguments: login, sha2 hashed password and path to config");
            return;
        }
        String login = args[0];
        String hashedPassword = args[1];
        String pathToConfig = args[2];
        try {
            SlaveServerRunner slaveServerRunner = new SlaveServerRunner();
            slaveServerRunner.run(pathToConfig, login, hashedPassword);
        } catch (Exception e) {
            printErr("Error occurred while startup");
            e.printStackTrace();
        }
    }

    private static void printErr(String msg) {
        System.err.println(msg);
    }

}
