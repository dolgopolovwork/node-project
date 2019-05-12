package ru.babobka.nodesecurity.auth;

import lombok.NonNull;

import java.security.PrivateKey;

/**
 * Created by 123 on 08.06.2018.
 */
public class AuthCredentials {
    private final String login;
    private final PrivateKey privateKey;

    public AuthCredentials(@NonNull String login, @NonNull PrivateKey privateKey) {
        this.login = login;
        this.privateKey = privateKey;
    }

    public String getLogin() {
        return login;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }
}
