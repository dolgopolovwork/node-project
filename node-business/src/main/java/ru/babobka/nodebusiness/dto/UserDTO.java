package ru.babobka.nodebusiness.dto;

import java.io.Serializable;

/**
 * Created by 123 on 09.08.2017.
 */

public class UserDTO implements Serializable {

    private static final long serialVersionUID = 3340802658169837956L;
    private String name;
    private String password;
    private Integer taskCount;
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(Integer taskCount) {
        this.taskCount = taskCount;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
