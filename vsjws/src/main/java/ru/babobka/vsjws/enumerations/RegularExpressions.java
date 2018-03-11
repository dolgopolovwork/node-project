package ru.babobka.vsjws.enumerations;

public enum RegularExpressions {

    FILE_NAME_PATTERN("^[A-Za-z0-9 _]*[A-Za-z0-9][A-Za-z0-9 _]*$"),

    URL_PATTERN("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");

    private final String expression;

    RegularExpressions(String expression) {
        this.expression = expression;
    }

    public String toString() {
        return expression;
    }

}
