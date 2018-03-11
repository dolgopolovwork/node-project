package ru.babobka.vsjws.enumerations;

/**
 * Created by dolgopolov.a on 29.12.15.
 */
public enum ContentType {

    HTML("text/html;"),

    JSON("application/json;"),

    PLAIN("text/plain;"),

    XML("text/xml;");

    private final String type;

    ContentType(String type) {
        this.type = type + "charset=UTF-8";
    }

    @Override
    public String toString() {
        return type;
    }


}
