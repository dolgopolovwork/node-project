package ru.babobka.nodebusiness.model;

import ru.babobka.nodesecurity.keypair.KeyDecoder;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Objects;


/**
 * Created by dolgopolov.a on 29.10.15.
 */
@Entity
@Table(name = "node_user")
public class User implements Serializable {
    private static final long serialVersionUID = 6569577055168857214L;
    @Id
    @Column(name = "USER_ID")
    private String id;
    @Column(name = "USER_NAME")
    private String name;
    @Column(name = "USER_PUBLIC_KEY_BASE64")
    private String publicKeyBase64;
    @Column(name = "USER_EMAIL")
    private String email;

    public PublicKey getPublicKey() throws InvalidKeySpecException {
        return KeyDecoder.decodePublicKey(this.publicKeyBase64);
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

    public String getPublicKeyBase64() {
        return publicKeyBase64;
    }

    public void setPublicKeyBase64(String publicKeyBase64) {
        this.publicKeyBase64 = publicKeyBase64;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(name, user.name) &&
                Objects.equals(publicKeyBase64, user.publicKeyBase64) &&
                Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, publicKeyBase64, email);
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", publicKeyBase64='" + publicKeyBase64 + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
