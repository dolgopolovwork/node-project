package ru.babobka.nodemasterserver.validation.config;

import ru.babobka.nodemasterserver.server.MasterServerConfig;
import ru.babobka.nodemasterserver.validation.config.rule.*;
import ru.babobka.nodeutils.validation.Validator;

import java.util.Arrays;

/**
 * Created by 123 on 19.08.2017.
 */
public class MasterServerConfigValidator extends Validator<MasterServerConfig> {

    public MasterServerConfigValidator() {
        super(Arrays.asList(
                new AuthTimeoutMillisValidationRule(),
                new HeartBeatTimeoutMillisValidationRule(),
                new LoggerFolderValidationRule(),
                new MainServerPortValidationRule(),
                new RequestTimeoutMillisValidationRule(),
                new TaskFolderValidationRule(),
                new WebPortValidationRule(),
                new SrpConfigValidationRule()));
    }
}
