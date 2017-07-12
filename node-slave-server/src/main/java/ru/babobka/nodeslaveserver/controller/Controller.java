package ru.babobka.nodeslaveserver.controller;

import java.io.IOException;

public interface Controller<C> {

    void control(C controlObject) throws IOException;

}
