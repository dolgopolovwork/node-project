package ru.babobka.nodebusiness.model;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;


/**
 * Created by dolgopolov.a on 29.10.15.
 */
public class User implements Serializable {

    private static final long serialVersionUID = 6569577055168857214L;
    private String name;
    private PublicKey publicKey;
    private String email;
    private UUID id;

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
        return Objects.equals(name, user.name) &&
                Objects.equals(publicKey, user.publicKey) &&
                Objects.equals(email, user.email) &&
                Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, publicKey, email, id);
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", publicKey=" + publicKey +
                ", email='" + email + '\'' +
                ", id=" + id +
                '}';
    }
}
