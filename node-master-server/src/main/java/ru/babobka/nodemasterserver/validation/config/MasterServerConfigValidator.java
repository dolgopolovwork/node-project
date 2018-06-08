package ru.babobka.nodemasterserver.validation.config;

import ru.babobka.nodemasterserver.server.config.MasterServerConfig;
import ru.babobka.nodemasterserver.validation.config.rule.FolderValidationRule;
import ru.babobka.nodemasterserver.validation.config.rule.PortValidationRule;
import ru.babobka.nodemasterserver.validation.config.rule.SecurityConfigValidationRule;
import ru.babobka.nodemasterserver.validation.config.rule.TimeValidationRule;
import ru.babobka.nodeutils.validation.Validator;

import java.util.Arrays;

/**
 * Created by 123 on 19.08.2017.
 */
public class MasterServerConfigValidator extends Validator<MasterServerConfig> {

    public MasterServerConfigValidator() {
        super(Arrays.asList(
                new FolderValidationRule(),
                new PortValidationRule(),
                new TimeValidationRule(),
                new SecurityConfigValidationRule()));
    }
}
