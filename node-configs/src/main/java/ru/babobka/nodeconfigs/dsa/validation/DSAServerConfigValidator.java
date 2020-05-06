package ru.babobka.nodeconfigs.dsa.validation;

import ru.babobka.nodeconfigs.dsa.DSAServerConfig;
import ru.babobka.nodeconfigs.dsa.validation.rule.KeyConfigValidationRule;
import ru.babobka.nodeconfigs.dsa.validation.rule.LoggerFolderConfigValidationRule;
import ru.babobka.nodeconfigs.dsa.validation.rule.PortConfigValidationRule;
import ru.babobka.nodeutils.validation.Validator;

import java.util.Arrays;

public class DSAServerConfigValidator extends Validator<DSAServerConfig> {

    public DSAServerConfigValidator() {
        super(Arrays.asList(
                new PortConfigValidationRule(),
                new KeyConfigValidationRule(),
                new LoggerFolderConfigValidationRule()));
    }
}
