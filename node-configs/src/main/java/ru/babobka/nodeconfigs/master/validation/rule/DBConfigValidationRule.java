package ru.babobka.nodeconfigs.master.validation.rule;

import ru.babobka.nodeconfigs.master.DBConfig;
import ru.babobka.nodeconfigs.master.MasterServerConfig;
import ru.babobka.nodeutils.util.TextUtil;
import ru.babobka.nodeutils.validation.ValidationRule;

public class DBConfigValidationRule implements ValidationRule<MasterServerConfig> {
    @Override
    public void validate(MasterServerConfig config) {
        if (config.getDbConfig() == null) {
            throw new IllegalArgumentException("db config was not set");
        }
        DBConfig dbConfig = config.getDbConfig();
        if (TextUtil.isEmpty(dbConfig.getHost())) {
            throw new IllegalArgumentException("db host was not set");
        } else if (!TextUtil.isValidPort(dbConfig.getPort())) {
            throw new IllegalArgumentException("db port " + dbConfig.getPort() + " is not valid");
        } else if (TextUtil.isEmpty((dbConfig.getUser()))) {
            throw new IllegalArgumentException("db user was not specified");
        } else if (TextUtil.isEmpty((dbConfig.getPassword()))) {
            throw new IllegalArgumentException("db password was not specified");
        }
    }
}
