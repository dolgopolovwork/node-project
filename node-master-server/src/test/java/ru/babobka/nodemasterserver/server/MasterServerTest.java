package ru.babobka.nodemasterserver.server;

import java.io.IOException;

import org.junit.Test;

import ru.babobka.nodeutils.container.ContainerStrategyException;

public class MasterServerTest {

	@Test
	public void initContainerTest() throws ContainerStrategyException, IOException {
		MasterServer.initTestContainer();
	}

	
}
