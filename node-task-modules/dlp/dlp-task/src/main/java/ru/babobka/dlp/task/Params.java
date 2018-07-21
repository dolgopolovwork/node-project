package ru.babobka.dlp.task;

/**
 * Created by 123 on 20.06.2017.
 */
public enum Params {
    EXP("exp"),
    X("x"),
    Y("y"),
    MOD("mod"),
    LOOPS("loops"),
    COLLISIONS("collisions"),
    GEN_EXP_LIST("genExpList"),
    VAL_EXP_LIST("valExpList");

    private final String value;

    Params(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
