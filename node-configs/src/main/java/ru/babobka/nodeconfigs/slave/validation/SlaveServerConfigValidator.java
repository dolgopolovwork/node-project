package ru.babobka.nodeconfigs.slave.validation;

import ru.babobka.nodeconfigs.slave.SlaveServerConfig;
import ru.babobka.nodeconfigs.slave.validation.rule.*;
import ru.babobka.nodeutils.validation.Validator;

import java.util.Arrays;

/**
 * Created by 123 on 05.11.2017.
 */
public class SlaveServerConfigValidator extends Validator<SlaveServerConfig> {
    public SlaveServerConfigValidator() {
        super(Arrays.asList(
                new CredentialsValidationRule(),
                new ServerPortValidationRule(),
                new AuthTimeoutMillisValidationRule(),
                new LoggerFolderValidationRule(),
                new RequestTimeoutMillisValidationRule(),
                new TasksFolderValidationRule(),
                new ServerPublicKeyValidationRule())
        );
    }
}
