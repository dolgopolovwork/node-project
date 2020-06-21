package ru.babobka.nodesecurity.auth;

import lombok.NonNull;

/**
 * Created by 123 on 08.06.2018.
 */
public class AuthCredentials {
    private final String login;

    public AuthCredentials(@NonNull String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

}
