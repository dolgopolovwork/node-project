package ru.babobka.vsjws.model;

public class Header {

    private final String key;

    private final String value;

    public Header(String headerLine) {
        String[] headerArray = headerLine.split(":");
        if (headerArray.length >= 2) {
            key = headerArray[0].trim();
            value = headerArray[1].trim();
        } else {
            throw new IllegalArgumentException("Invalid header line: " + headerLine);
        }
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }


}
