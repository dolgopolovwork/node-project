package ru.babobka.primecounter.task;

/**
 * Created by 123 on 20.06.2017.
 */
public enum Params {
    BEGIN("begin"),
    END("end"),
    PRIME_COUNT("primeCount");

    private final String value;

    Params(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
