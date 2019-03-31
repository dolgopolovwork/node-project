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
import ru.babobka.nodesecurity.rsa.RSAConfigFactory;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.math.SafePrime;
import ru.babobka.nodeutils.time.TimerInvoker;
import ru.babobka.nodeutils.util.StreamUtil;
import ru.babobka.nodeutils.util.TextUtil;
import ru.babobka.nodeutils.validation.ValidationRule;

import java.io.File;
import java.io.IOException;
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
        boolean mustBeEncrypted = readYesNo("do you want your config to be encrypted?");
        String password = null;
        if (mustBeEncrypted) {
            password = readPassword("please provide password to encrypt configuration file.");
        }
        configProvider.createConfig(configFolder, config, password);
        print("Congratulations! Your config file was successfully created in " + configFolder);
    }

    private void setModeConfig(MasterServerConfig config) {
        printLabel("Modes configuration");
        ModeConfig modeConfig = new ModeConfig();
        modeConfig.setCacheMode(readYesNo("enable response caching?"));
        modeConfig.setSingleSessionMode(readYesNo("don't let clients connect with the same credentials twice?"));
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
                    "please provide port number for clients", validPortRule)));
            portConfig.setSlaveListenerPort(Integer.parseInt(readLine(
                    "please provide port number for slaves", validPortRule)));
            portConfig.setWebListenerPort(Integer.parseInt(readLine(
                    "please provide port number for web-server", validPortRule)));
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
                    "please provide timeout for authentication service (in seconds)", validNumber)) * 1000);
            timeConfig.setDataOutDateMillis(Integer.parseInt(readLine(
                    "please provide timeout for transmitted data to be considered outdated (in seconds)", validNumber)) * 1000);
            timeConfig.setHeartBeatCycleMillis(Integer.parseInt(readLine(
                    "please provide frequency of heart beating (in seconds)", validNumber)) * 1000);
            timeConfig.setRequestReadTimeOutMillis(Integer.parseInt(readLine(
                    "please provide data reading timeout (in seconds)", validNumber)) * 1000);
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
            folderConfig.setLoggerFolder(readLine("please provide path to log folder"));
            folderConfig.setTasksFolder(readLine("please provide path to folder full of tasks"));
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
        SecurityConfig securityConfig = new SecurityConfig();
        securityConfig.setBigSafePrime(SafePrime.random((256)).getPrime());
        securityConfig.setChallengeBytes(32);
        securityConfig.setRsaConfig(RSAConfigFactory.create(256));
        config.setSecurity(securityConfig);
    }

    @Override
    public String getAppName() {
        return "master-config-writer";
    }
}
