package ru.babobka.vsjws.enumerations;

/**
 * Created by 123 on 12.06.2017.
 */
public enum RestrictedHeader {

    SERVER("Server"),
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    CONNECTION("Connection");

    private final String text;

    RestrictedHeader(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
