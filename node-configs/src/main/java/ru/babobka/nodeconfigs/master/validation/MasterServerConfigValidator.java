package ru.babobka.nodeconfigs.master.validation;

import ru.babobka.nodeconfigs.master.MasterServerConfig;
import ru.babobka.nodeconfigs.master.validation.rule.FolderValidationRule;
import ru.babobka.nodeconfigs.master.validation.rule.PortValidationRule;
import ru.babobka.nodeconfigs.master.validation.rule.KeyConfigValidationRule;
import ru.babobka.nodeconfigs.master.validation.rule.TimeValidationRule;
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
                new KeyConfigValidationRule()));
    }
}
