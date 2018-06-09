package ru.babobka.nodesecurity.auth;

/**
 * Created by 123 on 08.06.2018.
 */
public class AuthCredentials {
    private final String login;
    private final String password;

    public AuthCredentials(String login, String password) {
        if (login == null) {
            throw new IllegalArgumentException("login is null");
        } else if (password == null) {
            throw new IllegalArgumentException("password is null");
        }
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }
}
