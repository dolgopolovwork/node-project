package ru.babobka.nodemasterserver.validation.config;

import ru.babobka.nodemasterserver.server.MasterServerConfig;
import ru.babobka.nodeutils.validation.ValidationRule;
import ru.babobka.nodeutils.validation.Validator;

/**
 * Created by 123 on 19.08.2017.
 */
public class MasterServerConfigValidator extends Validator<MasterServerConfig> {

    public MasterServerConfigValidator(ValidationRule<MasterServerConfig>... rules) {
        super(rules);
    }
}
