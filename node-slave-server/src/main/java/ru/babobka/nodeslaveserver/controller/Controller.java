package ru.babobka.nodeslaveserver.controller;

public abstract class Controller<C> {

    public void control(C controlObject) {
        if (controlObject == null) {
            throw new IllegalArgumentException("cannot control null object");
        }
        controlImpl(controlObject);
    }

    protected abstract void controlImpl(C controlObject);

}
