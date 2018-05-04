package ru.babobka.nodesecurity.auth;

import ru.babobka.nodesecurity.config.SrpConfig;
import ru.babobka.nodeutils.util.ArrayUtil;

import java.io.Serializable;

/**
 * Created by 123 on 30.04.2018.
 */
public class AuthData implements Serializable {
    private static final long serialVersionUID = 3561820630535087705L;
    private final SrpConfig srpConfig;
    private final byte[] salt;

    public AuthData(SrpConfig srpConfig, byte[] salt) {
        if (srpConfig == null) {
            throw new IllegalArgumentException("srpConfig is null");
        } else if (ArrayUtil.isEmpty(salt)) {
            throw new IllegalArgumentException("salt is empty");
        }
        this.srpConfig = srpConfig;
        this.salt = salt.clone();
    }

    public SrpConfig getSrpConfig() {
        return srpConfig;
    }

    public byte[] getSalt() {
        return salt.clone();
    }
}
