package ru.babobka.nodeconfigs.cli.master;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import ru.babobka.nodeclient.console.CLI;
import ru.babobka.nodeconfigs.ConfigsApplicationContainer;
import ru.babobka.nodeconfigs.master.*;
import ru.babobka.nodeconfigs.master.validation.rule.FolderValidationRule;
import ru.babobka.nodeconfigs.master.validation.rule.PortValidationRule;
import ru.babobka.nodeconfigs.master.validation.rule.TimeValidationRule;
import ru.babobka.nodeconfigs.service.ConfigProvider;
import ru.babobka.nodesecurity.SecurityApplicationContainer;
import ru.babobka.nodesecurity.keypair.Base64KeyPair;
import ru.babobka.nodesecurity.keypair.KeyDecoder;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.time.TimerInvoker;
import ru.babobka.nodeutils.util.StreamUtil;
import ru.babobka.nodeutils.util.TextUtil;
import ru.babobka.nodeutils.validation.ValidationRule;

import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.util.Collections;
import java.util.List;

/**
 * Created by 123 on 16.09.2018.
 */
public class MasterConfigWriterMain extends CLI {

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
    public List<Option> createOptions() {
        return Collections.emptyList();
    }

    @Override
    public void run(CommandLine cmd) throws Exception {
        MasterServerConfig masterServerConfig = new MasterServerConfig();
        setModeConfig(masterServerConfig);
        setPortConfig(masterServerConfig);
        setTimeConfig(masterServerConfig);
        setFolderConfig(masterServerConfig);
        setSecurityConfig(masterServerConfig);
        setRmqConfig(masterServerConfig);
        writeConfig(masterServerConfig);
    }

    private void writeConfig(MasterServerConfig config) throws IOException {
        printLabel("Saving");
        String configFolder;
        while (true) {
            configFolder = readLine("please set folder where newly created configuration will be written");
            File folder = new File(configFolder);
            if (folder.exists() && folder.isDirectory()) {
                break;
            } else {
                printErr("folder '" + configFolder + "' doesn't exist");
            }
        }
        configProvider.createMasterConfig(configFolder, config);
        print("Congratulations! Your config file was successfully created in " + configFolder);
    }

    private void setModeConfig(MasterServerConfig config) {
        printLabel("Modes configuration");
        ModeConfig modeConfig = new ModeConfig();
        modeConfig.setCacheMode(readYesNo("enable response caching?"));
        modeConfig.setSingleSessionMode(!readYesNo("enable multiple sessions?"));
        modeConfig.setTestUserMode(readYesNo("WARNING! don't use this ability in production.\ncreate test user?"));
        config.setModes(modeConfig);
    }

    private void setPortConfig(MasterServerConfig config) {
        printLabel("Ports configuration");
        PortConfig portConfig = new PortConfig();
        ValidationRule<String> validPortRule = port -> {
            if (!TextUtil.isValidPort(port)) {
                throw new IllegalArgumentException("invalid port");
            }
        };
        PortValidationRule totalPortValidationRule = new PortValidationRule();
        while (true) {
            portConfig.setClientListenerPort(Integer.parseInt(readLine(
                    "port number for clients", validPortRule)));
            portConfig.setSlaveListenerPort(Integer.parseInt(readLine(
                    "port number for slaves", validPortRule)));
            portConfig.setWebListenerPort(Integer.parseInt(readLine(
                    "port number for web-server", validPortRule)));
            try {
                config.setPorts(portConfig);
                totalPortValidationRule.validate(config);
                break;
            } catch (RuntimeException e) {
                printErr(e.getMessage());
            }
        }
    }

    private void setTimeConfig(MasterServerConfig config) {
        printLabel("Time configuration");
        TimeConfig timeConfig = new TimeConfig();
        ValidationRule<String> validNumber = number -> {
            if (!TextUtil.isNumber(number) || TextUtil.tryParseInt(number) <= 0) {
                throw new IllegalArgumentException("invalid time");
            }
        };
        TimeValidationRule timeValidationRule = new TimeValidationRule();
        while (true) {
            timeConfig.setAuthTimeOutMillis(Integer.parseInt(readLine(
                    "timeout for authentication service (in seconds)", validNumber)) * 1000);
            timeConfig.setDataOutDateMillis(Integer.parseInt(readLine(
                    "timeout for transmitted data to be considered outdated (in seconds)", validNumber)) * 1000);
            timeConfig.setHeartBeatCycleMillis(Integer.parseInt(readLine(
                    "frequency of heart beating (in seconds)", validNumber)) * 1000);
            timeConfig.setRequestReadTimeOutMillis(Integer.parseInt(readLine(
                    "data reading timeout (in seconds)", validNumber)) * 1000);
            try {
                config.setTime(timeConfig);
                timeValidationRule.validate(config);
                break;
            } catch (RuntimeException e) {
                printErr(e.getMessage());
            }
        }
    }

    private void setFolderConfig(MasterServerConfig config) {
        printLabel("Folders configuration");
        print("you can specify folders using env variables. use dollar sign($). for example: $TEST_VAR");
        FolderConfig folderConfig = new FolderConfig();
        FolderValidationRule folderValidationRule = new FolderValidationRule();
        while (true) {
            folderConfig.setLoggerFolder(readLine("path to log folder"));
            folderConfig.setTasksFolder(readLine("path to folder full of tasks"));
            try {
                config.setFolders(folderConfig);
                folderValidationRule.validate(config);
                break;
            } catch (RuntimeException e) {
                printErr(e.getMessage());
            }
        }
    }

    private void setSecurityConfig(MasterServerConfig config) {
        KeyPair keyPair = KeyDecoder.generateKeyPair();
        Base64KeyPair base64KeyPair = new Base64KeyPair();
        base64KeyPair.setPrivKey(TextUtil.toBase64(keyPair.getPrivate().getEncoded()));
        base64KeyPair.setPubKey(TextUtil.toBase64(keyPair.getPublic().getEncoded()));
        config.setKeyPair(base64KeyPair);
    }

    private void setRmqConfig(MasterServerConfig config) {
        printLabel("RMQ configuration");
        ValidationRule<String> validPortRule = port -> {
            if (!TextUtil.isValidPort(port)) {
                throw new IllegalArgumentException("invalid port");
            }
        };
        if (readYesNo("enable rpc?")) {
            RmqConfig rmqConfig = new RmqConfig();
            rmqConfig.setHost(readLine("rmq host"));
            rmqConfig.setPort(Integer.parseInt(readLine("rmq port", validPortRule)));
            config.setRmqConfig(rmqConfig);
        }
    }

    @Override
    public String getAppName() {
        return "master-config-writer";
    }
}
