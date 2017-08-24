package ru.babobka.nodeserials;

import ru.babobka.nodeutils.util.HashUtil;

import java.io.Serializable;

/**
 * Created by 123 on 19.08.2017.
 */
public class NodeAuthRequest implements Serializable {
    private static final long serialVersionUID = -8625209279110892295L;
    private final String login;
    private final byte[] hashedPassword;

    public NodeAuthRequest(String login, String password) {
        if (login == null) {
            throw new IllegalArgumentException("login is null");
        } else if (password == null) {
            throw new IllegalArgumentException("password is null");
        }
        this.login = login;
        this.hashedPassword = HashUtil.sha2(password);
    }

    public String getLogin() {
        return login;
    }

    public byte[] getHashedPassword() {
        return hashedPassword.clone();
    }
}
