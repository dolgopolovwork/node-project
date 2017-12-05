package ru.babobka.nodemasterserver.validation.config.rule;

import ru.babobka.nodemasterserver.server.MasterServerConfig;
import ru.babobka.nodeutils.util.TextUtil;
import ru.babobka.nodeutils.validation.ValidationRule;

/**
 * Created by 123 on 26.07.2017.
 */
public class RestServiceLoginValidationRule implements ValidationRule<MasterServerConfig> {
    @Override
    public void validate(MasterServerConfig data) {
        if (TextUtil.isEmpty(data.getRestServiceLogin())) {
            throw new IllegalArgumentException("'restServiceLogin' must not be null");
        }
    }
}
