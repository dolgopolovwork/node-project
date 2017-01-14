package ru.babobka.nodemasterserver.server;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.babobka.nodemasterserver.builder.TestUserBuilder;
import ru.babobka.nodemasterserver.slave.SlavesStorage;
import ru.babobka.nodeslaveserver.exception.MasterServerIsFullException;
import ru.babobka.nodeslaveserver.exception.SlaveAuthFailException;
import ru.babobka.nodeslaveserver.server.SlaveServer;
import ru.babobka.nodeslaveserver.server.SlaveServerContainerStrategy;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.util.StreamUtil;

public class ServerAuthITCase {

	/*
	 * static { new MasterServerContainerStrategy(
	 * StreamUtil.getLocalResource(MasterServer.class,
	 * MasterServer.MASTER_SERVER_TEST_CONFIG))
	 * .contain(Container.getInstance()); new SlaveServerContainerStrategy(
	 * StreamUtil.getLocalResource(SlaveServer.class,
	 * SlaveServer.SLAVE_SERVER_TEST_CONFIG)) .contain(Container.getInstance());
	 * }
	 */

	private static MasterServerConfig config;

	private static SlavesStorage slavesStorage;

	private static SlaveServer[] slaveServers;

	private static final int SLAVES = 5;

	private static MasterServer masterServer;

	private static final String LOGIN = TestUserBuilder.LOGIN;

	private static final String PASSWORD = TestUserBuilder.PASSWORD;

	private static final int TESTS = 5;

	@BeforeClass
	public static void runMasterServer() throws IOException {
		MasterServer.initTestContainer();
		SlaveServer.initTestContainer();

		config = Container.getInstance().get(MasterServerConfig.class);

		slavesStorage = Container.getInstance().get(SlavesStorage.class);

		masterServer = new MasterServer();
		masterServer.start();
	}

	@After
	public void tearDown() throws InterruptedException {
		closeSlaves();
	}

	@Test
	public void logInMass() throws IOException, InterruptedException {
		for (int i = 0; i < TESTS; i++) {
			createSlaves(config.getMaxSlaves());
			startSlaves();
			assertEquals(slavesStorage.getClusterSize(), SLAVES);
			closeSlaves();
		}
	}

	@Test(expected = MasterServerIsFullException.class)
	public void logInTooMuch() throws IOException {

		createSlaves(config.getMaxSlaves() + 1);

	}

	@Test
	public void logOutMass() throws IOException, InterruptedException {
		for (int i = 0; i < TESTS; i++) {
			createSlaves(SLAVES);
			startSlaves();
			closeSlaves();
			Thread.sleep(200);
			assertEquals(slavesStorage.getClusterSize(), 0);
		}
	}

	@Test(expected = IOException.class)
	public void logFailBadAddress() throws IOException {

		new SlaveServer("localhost123", config.getMainServerPort(), "test_user", "abc");

	}

	@Test
	public void logFailBadLogin() throws IOException {

		try {
			new SlaveServer("localhost", config.getMainServerPort(), LOGIN + "abc", PASSWORD);
			fail();
		} catch (SlaveAuthFailException e) {

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

	}

	public static void createSlaves(int size) throws IOException {
		slaveServers = new SlaveServer[size];
		for (int i = 0; i < slaveServers.length; i++) {
			slaveServers[i] = new SlaveServer("localhost", config.getMainServerPort(), LOGIN, PASSWORD);
		}
	}

	public static void startSlaves() {
		for (int i = 0; i < slaveServers.length; i++) {
			slaveServers[i].start();
		}
	}

	public static void closeSlaves() throws InterruptedException {
		if (slaveServers != null) {
			for (int i = 0; i < slaveServers.length; i++) {
				if (slaveServers[i] != null) {
					slaveServers[i].interrupt();
					slaveServers[i].join();
				}
			}

		}

	}

	@AfterClass
	public static void closeMasterServer() {

		if (masterServer != null)
			masterServer.interrupt();
		try {
			if (masterServer != null)
				masterServer.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
