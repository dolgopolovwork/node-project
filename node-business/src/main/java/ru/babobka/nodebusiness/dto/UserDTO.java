package ru.babobka.nodebusiness.dto;

import java.io.Serializable;

/**
 * Created by 123 on 09.08.2017.
 */

public class UserDTO implements Serializable {

    private static final long serialVersionUID = 3340802658169837956L;
    private String name;
    private String hashedPassword;
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
