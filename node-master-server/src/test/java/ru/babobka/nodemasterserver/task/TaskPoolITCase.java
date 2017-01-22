package ru.babobka.nodemasterserver.task;

import static org.junit.Assert.assertFalse;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import ru.babobka.nodemasterserver.server.MasterServer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.container.ContainerStrategyException;


public class TaskPoolITCase {

	
	@BeforeClass
	public static void setUp() throws ContainerStrategyException, FileNotFoundException
	{
		MasterServer.initTestContainer();
		
	}

	@Test
	public void testEquality() throws IOException {
		TaskPool pool = Container.getInstance().get(TaskPool.class);
		Set<String> keySet = pool.getTasksMap().keySet();
		assertFalse(keySet.isEmpty());
		String taskName = keySet.iterator().next();
		assertFalse(pool.get(taskName) == pool.get(taskName));
	}
}
