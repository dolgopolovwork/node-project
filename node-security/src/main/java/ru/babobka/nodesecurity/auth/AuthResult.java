package ru.babobka.nodesecurity.auth;

import ru.babobka.nodeutils.util.ArrayUtil;

/**
 * Created by 123 on 30.04.2018.
 */
public class AuthResult {
    private final byte[] secretKey;
    private final boolean success;

    private AuthResult(byte[] secretKey, boolean success) {
        if (ArrayUtil.isEmpty(secretKey)) {
            throw new IllegalArgumentException("secretKey is empty");
        }
        this.secretKey = secretKey.clone();
        this.success = success;
    }

    public static AuthResult success(byte[] secretKey) {
        return new AuthResult(secretKey, true);
    }

    public static AuthResult fail() {
        return new AuthResult(new byte[]{0}, false);
    }

    public byte[] getSecretKey() {
        return secretKey.clone();
    }

    public boolean isSuccess() {
        return success;
    }
}
