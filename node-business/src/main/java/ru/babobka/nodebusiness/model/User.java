package ru.babobka.nodebusiness.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.UUID;


/**
 * Created by dolgopolov.a on 29.10.15.
 */
public class User implements Serializable {

    private static final long serialVersionUID = 6569577055168857214L;
    private String name;
    private byte[] secret;
    private byte[] salt;
    private String email;
    private UUID id;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public byte[] getSecret() {
        if (secret != null)
            return secret.clone();
        return new byte[]{};
    }

    public void setSecret(byte[] secret) {
        if (secret != null)
            this.secret = secret.clone();
    }

    public byte[] getSalt() {
        if (salt != null)
            return salt.clone();
        return new byte[]{};
    }

    public void setSalt(byte[] salt) {
        if (salt != null)
            this.salt = salt.clone();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        if (!Arrays.equals(secret, user.secret)) return false;
        if (!Arrays.equals(salt, user.salt)) return false;
        if (email != null ? !email.equals(user.email) : user.email != null) return false;
        return id != null ? id.equals(user.id) : user.id == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(secret);
        result = 31 * result + Arrays.hashCode(salt);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", secret=" + Arrays.toString(secret) +
                ", salt=" + Arrays.toString(salt) +
                ", email='" + email + '\'' +
                ", id=" + id +
                '}';
    }
}
