package ru.babobka.nodeslaveserver.server;

import java.io.FileNotFoundException;

import org.junit.Test;

import ru.babobka.nodeutils.container.ContainerException;

public class SlaveServerTest {

    @Test
    public void initContainerTest() throws ContainerException, FileNotFoundException {
	SlaveServer.initTestContainer();
    }
}
