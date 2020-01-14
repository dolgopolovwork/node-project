package ru.babobka.nodebusiness.dto;

import java.io.Serializable;

/**
 * Created by 123 on 09.08.2017.
 */

public class UserDTO implements Serializable {

    private static final long serialVersionUID = 3340802658169837956L;
    private String name;
    private String base64PubKey;
    private String email;
    private String id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBase64PubKey() {
        return base64PubKey;
    }

    public void setBase64PubKey(String base64PubKey) {
        this.base64PubKey = base64PubKey;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
