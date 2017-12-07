package ru.babobka.nodeserials;

import java.io.Serializable;

/**
 * Created by 123 on 19.08.2017.
 */
public class NodeAuthRequest implements Serializable {
    private static final long serialVersionUID = -8625209279110892295L;
    private final String login;
    private final String hashedPassword;

    public NodeAuthRequest(String login, String hashedPassword) {
        if (login == null) {
            throw new IllegalArgumentException("login is null");
        } else if (hashedPassword == null) {
            throw new IllegalArgumentException("hashedPassword is null");
        }
        this.login = login;
        this.hashedPassword = hashedPassword;
    }

    public String getLogin() {
        return login;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }
}
