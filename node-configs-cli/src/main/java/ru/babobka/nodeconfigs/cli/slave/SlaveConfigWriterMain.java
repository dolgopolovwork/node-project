package ru.babobka.nodeconfigs.cli.slave;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import ru.babobka.nodeclient.console.CLI;
import ru.babobka.nodeconfigs.ConfigsApplicationContainer;
import ru.babobka.nodeconfigs.service.ConfigProvider;
import ru.babobka.nodeconfigs.slave.SlaveServerConfig;
import ru.babobka.nodeconfigs.slave.validation.rule.LoggerFolderValidationRule;
import ru.babobka.nodeconfigs.slave.validation.rule.TasksFolderValidationRule;
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
import java.security.spec.InvalidKeySpecException;
import java.util.Collections;
import java.util.List;

/**
 * Created by 123 on 16.09.2018.
 */
public class SlaveConfigWriterMain extends CLI {

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
        SlaveServerConfig slaveServerConfig = new SlaveServerConfig();
        /*
    private String serverBase64PublicKey;
         */
        setHostPortConfig(slaveServerConfig);
        setTimeConfig(slaveServerConfig);
        setFolderConfig(slaveServerConfig);
        setSecurityConfig(slaveServerConfig);
        setLoginConfig(slaveServerConfig);
        setServerBase64PubKey(slaveServerConfig);
        writeConfig(slaveServerConfig);
    }

    private void writeConfig(SlaveServerConfig config) throws IOException {
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
        configProvider.createSlaveConfig(configFolder, config);
        print("Congratulations! Your config file was successfully created in " + configFolder);
    }

    private void setHostPortConfig(SlaveServerConfig config) {
        printLabel("Host/port configuration");

        ValidationRule<String> validPortRule = port -> {
            if (!TextUtil.isValidPort(port)) {
                throw new IllegalArgumentException("invalid port");
            }
        };
        config.setServerHost(readLine("sever host name or ip address"));
        config.setServerPort(Integer.parseInt(readLine("sever port number", validPortRule)));
    }

    private void setLoginConfig(SlaveServerConfig config) {
        printLabel("Login configuration");
        config.setSlaveLogin(readLine("login"));
    }

    private void setServerBase64PubKey(SlaveServerConfig config) {
        printLabel("Server pubkey configuration");
        ValidationRule<String> validBase64Key = base64PubKey -> {
            if (TextUtil.isEmpty(base64PubKey)) {
                throw new IllegalArgumentException("server pub key cannot be empty");
            }
            try {
                KeyDecoder.decodePublicKey(base64PubKey);
            } catch (InvalidKeySpecException e) {
                throw new IllegalArgumentException("invalid pub key");
            }
        };
        config.setServerBase64PublicKey(readLine("server base64 pubkey", validBase64Key));
    }

    private void setTimeConfig(SlaveServerConfig config) {
        printLabel("Time configuration");
        ValidationRule<String> validNumber = number -> {
            if (!TextUtil.isNumber(number) || TextUtil.tryParseInt(number) <= 0) {
                throw new IllegalArgumentException("invalid time");
            }
        };
        config.setAuthTimeOutMillis(Integer.parseInt(readLine(
                "timeout for authentication service (in seconds)", validNumber)) * 1000);
        config.setRequestTimeoutMillis((Integer.parseInt(readLine(
                "timeout for transmitted data to be considered outdated (in seconds)", validNumber)) * 1000));
    }

    private void setFolderConfig(SlaveServerConfig config) {
        printLabel("Folders configuration");
        print("you can specify folders using env variables. use dollar sign($). for example: $TEST_VAR");
        LoggerFolderValidationRule loggerFolderValidationRule = new LoggerFolderValidationRule();
        TasksFolderValidationRule tasksFolderValidationRule = new TasksFolderValidationRule();
        while (true) {
            config.setLoggerFolder(readLine("path to log folder"));
            config.setTasksFolder(readLine("path to folder full of tasks"));
            try {
                loggerFolderValidationRule.validate(config);
                tasksFolderValidationRule.validate(config);
                break;
            } catch (RuntimeException e) {
                printErr(e.getMessage());
            }
        }
    }

    private void setSecurityConfig(SlaveServerConfig config) {
        KeyPair keyPair = KeyDecoder.generateKeyPair();
        Base64KeyPair base64KeyPair = new Base64KeyPair();
        base64KeyPair.setPrivKey(TextUtil.toBase64(keyPair.getPrivate().getEncoded()));
        base64KeyPair.setPubKey(TextUtil.toBase64(keyPair.getPublic().getEncoded()));
        config.setKeyPair(base64KeyPair);
    }

    @Override
    public String getAppName() {
        return "slave-config-writer";
    }
}
