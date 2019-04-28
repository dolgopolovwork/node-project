package ru.babobka.nodeconfigs.cli;

import ru.babobka.nodeclient.console.CLI;
import ru.babobka.nodeconfigs.cli.master.MasterConfigEncryptionMain;
import ru.babobka.nodeconfigs.cli.master.MasterConfigReaderMain;
import ru.babobka.nodeconfigs.cli.master.MasterConfigWriterMain;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 123 on 29.09.2018.
 */
public class ConfigCliMainApp {
    private static final Map<String, CLI> COMMANDS = new HashMap<>();

    static {
        addCommand(new MasterConfigEncryptionMain());
        addCommand(new MasterConfigReaderMain());
        addCommand(new MasterConfigWriterMain());
    }

    private static void addCommand(CLI cli) {
        COMMANDS.put(cli.getAppName(), cli);
    }

    public static void main(String[] args) {
        String command = args.length > 0 ? args[0] : null;
        CLI commandRunnable = COMMANDS.get(command);
        if (commandRunnable == null) {
            CLI.printErr("You must specify a valid command to execute.\nAvailable commands: " + COMMANDS.keySet());
            return;
        }
        commandRunnable.onStart(Arrays.copyOfRange(args, 1, args.length));
    }
}
