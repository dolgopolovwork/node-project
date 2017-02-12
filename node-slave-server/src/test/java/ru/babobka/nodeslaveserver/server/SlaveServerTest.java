package ru.babobka.nodeslaveserver.server;

import java.io.FileNotFoundException;

import org.junit.Test;

import ru.babobka.nodeutils.container.ContainerStrategyException;

public class SlaveServerTest {

    @Test
    public void initContainerTest() throws ContainerStrategyException, FileNotFoundException {
	SlaveServer.initTestContainer();
    }
}
