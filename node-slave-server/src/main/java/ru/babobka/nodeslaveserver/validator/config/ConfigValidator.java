package ru.babobka.nodeslaveserver.validator.config;

import ru.babobka.nodeslaveserver.server.SlaveServerConfig;
import ru.babobka.nodeutils.validation.ValidationRule;
import ru.babobka.nodeutils.validation.Validator;

/**
 * Created by 123 on 05.11.2017.
 */
public class ConfigValidator extends Validator<SlaveServerConfig> {
    public ConfigValidator(ValidationRule<SlaveServerConfig>... rules) {
        super(rules);
    }
}
