package ru.babobka.nodebusiness.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.UUID;


/**
 * Created by dolgopolov.a on 29.10.15.
 */
public final class User implements Serializable {

    private static final long serialVersionUID = 6569577055168857213L;

    private String name;

    private byte[] hashedPassword;

    private Integer taskCount;

    private String email;

    private UUID id;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public byte[] getHashedPassword() {
        return hashedPassword == null ? null : hashedPassword.clone();
    }

    public void setHashedPassword(byte[] hashedPassword) {
        if (hashedPassword != null)
            this.hashedPassword = hashedPassword.clone();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(Integer taskCount) {
        this.taskCount = taskCount;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (name != null ? !name.equals(user.name) : user.name != null) return false;
        if (!Arrays.equals(hashedPassword, user.hashedPassword)) return false;
        if (taskCount != null ? !taskCount.equals(user.taskCount) : user.taskCount != null) return false;
        return (email != null ? email.equals(user.email) : user.email == null) && (id != null ? id.equals(user.id) : user.id == null);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(hashedPassword);
        result = 31 * result + (taskCount != null ? taskCount.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }
}
