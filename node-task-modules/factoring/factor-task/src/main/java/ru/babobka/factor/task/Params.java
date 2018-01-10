package ru.babobka.factor.task;

/**
 * Created by 123 on 20.06.2017.
 */
public enum Params {

    NUMBER("number"),
    FACTOR("factor"),
    X("x"),
    Y("y"),
    CURVE("curve");

    private final String value;

    Params(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
