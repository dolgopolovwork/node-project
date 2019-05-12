package ru.babobka.nodesecurity.auth;

import lombok.NonNull;
import ru.babobka.nodeutils.util.TextUtil;

import java.security.PublicKey;

/**
 * Created by 123 on 30.04.2018.
 */
public class AuthResult {
    private final PublicKey publicKey;
    private final String userName;
    private final boolean success;

    private AuthResult(String userName, PublicKey publicKey, boolean success) {
        if (success) {
            if (userName == null) {
                throw new NullPointerException("userName is null");
            } else if (publicKey == null) {
                throw new NullPointerException("publicKey is null");
            }
        }
        this.publicKey = publicKey;
        this.success = success;
        this.userName = userName;
    }

    public static AuthResult success(@NonNull String userName, PublicKey publicKey) {
        if (TextUtil.isEmpty(userName)) {
            throw new IllegalArgumentException("userName is empty");
        }
        return new AuthResult(userName, publicKey, true);
    }

    public static AuthResult fail() {
        return new AuthResult(null, null, false);
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getUserName() {
        return userName;
    }
}
