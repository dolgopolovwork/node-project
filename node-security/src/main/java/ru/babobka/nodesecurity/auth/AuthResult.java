package ru.babobka.nodesecurity.auth;

import ru.babobka.nodeutils.util.ArrayUtil;
import ru.babobka.nodeutils.util.TextUtil;

/**
 * Created by 123 on 30.04.2018.
 */
public class AuthResult {
    private final byte[] secretKey;
    private final String userName;
    private final boolean success;

    private AuthResult(String userName, byte[] secretKey, boolean success) {
        if (secretKey != null) {
            this.secretKey = secretKey.clone();
        } else {
            this.secretKey = null;
        }
        this.success = success;
        this.userName = userName;
    }

    public static AuthResult success(String userName, byte[] secretKey) {
        if (TextUtil.isEmpty(userName)) {
            throw new IllegalArgumentException("userName is empty");
        } else if (ArrayUtil.isEmpty(secretKey)) {
            throw new IllegalArgumentException("secretKey is empty");
        }
        return new AuthResult(userName, secretKey, true);
    }

    public static AuthResult fail() {
        return new AuthResult(null, null, false);
    }

    public byte[] getSecretKey() {
        return secretKey.clone();
    }

    public boolean isSuccess() {
        return success;
    }

    public String getUserName() {
        return userName;
    }
}
