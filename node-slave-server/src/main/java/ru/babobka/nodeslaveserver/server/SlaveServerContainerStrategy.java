package ru.babobka.nodeslaveserver.server;

import java.io.IOException;
import java.io.InputStream;

import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.container.ContainerStrategy;
import ru.babobka.nodeutils.container.ContainerStrategyException;
import ru.babobka.nodeslaveserver.builder.JSONFileServerConfigBuilder;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeslaveserver.service.SlaveAuthService;
import ru.babobka.nodeslaveserver.task.TaskPool;

public class SlaveServerContainerStrategy implements ContainerStrategy {

	private final SlaveServerConfig slaveServerConfig;

	public SlaveServerContainerStrategy(InputStream configFileInputStream) {
		this.slaveServerConfig = JSONFileServerConfigBuilder.build(configFileInputStream);
	}

	@Override
	public void contain(Container container) throws ContainerStrategyException {
		try {

			container.put(slaveServerConfig);
			container.put(new SimpleLogger("slave", slaveServerConfig.getLoggerFolder(), "slave"));
			container.put(new TaskPool());
			container.put(new SlaveAuthService());
			SimpleLogger logger = container.get(SimpleLogger.class);
			logger.log("Container was successfully initialized");
		} catch (IOException | RuntimeException e) {
			throw new ContainerStrategyException(e);
		}
	}

}
