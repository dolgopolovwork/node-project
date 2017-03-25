package ru.babobka.nodemasterserver.server;

import java.io.IOException;

import org.junit.Test;

import ru.babobka.nodeutils.container.ContainerException;

public class MasterServerTest {

    @Test
    public void initContainerTest() throws ContainerException, IOException {
	MasterServer.initTestContainer();
    }

}
