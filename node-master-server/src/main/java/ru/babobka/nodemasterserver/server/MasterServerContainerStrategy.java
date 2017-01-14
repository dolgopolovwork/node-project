package ru.babobka.nodemasterserver.server;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.container.ContainerStrategy;
import ru.babobka.nodeutils.container.ContainerStrategyException;
import ru.babobka.nodemasterserver.builder.JSONFileServerConfigBuilder;
import ru.babobka.nodemasterserver.dao.CacheDAOImpl;
import ru.babobka.nodemasterserver.dao.DebugCacheDAOImpl;
import ru.babobka.nodemasterserver.dao.DebugNodeUsersDAOImpl;
import ru.babobka.nodemasterserver.dao.NodeUsersDAOImpl;
import ru.babobka.nodemasterserver.datasource.RedisDatasource;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodemasterserver.model.ResponseStorage;
import ru.babobka.nodemasterserver.service.MasterAuthService;
import ru.babobka.nodemasterserver.service.CacheServiceImpl;
import ru.babobka.nodemasterserver.service.DistributionService;
import ru.babobka.nodemasterserver.service.NodeUsersServiceImpl;
import ru.babobka.nodemasterserver.service.TaskServiceImpl;
import ru.babobka.nodemasterserver.slave.SlavesStorage;
import ru.babobka.nodemasterserver.task.TaskPool;

public class MasterServerContainerStrategy implements ContainerStrategy {

	private final MasterServerConfig masterServerConfig;

	public MasterServerContainerStrategy(InputStream configFileInputStream) {
		this.masterServerConfig = JSONFileServerConfigBuilder.build(configFileInputStream);
	}

	@Override
	public void contain(Container container) throws ContainerStrategyException {
		try {

			container.put(masterServerConfig);
			container.put(StandardCharsets.UTF_8);
			container.put(new SimpleLogger("master", masterServerConfig.getLoggerFolder(), "master"));
			container.put(new ResponseStorage());
			container.put(new SlavesStorage(masterServerConfig.getMaxSlaves()));
			container.put(new DistributionService());
			container.put(new TaskPool());
			container.put(new TaskServiceImpl());

			if (masterServerConfig.isDebugDataBase()) {
				container.put(new DebugCacheDAOImpl());
				container.put(new DebugNodeUsersDAOImpl());
			} else {
				if (masterServerConfig.isProductionDataBase()) {
					container.put(new RedisDatasource(RedisDatasource.DatabaseNumber.PRODUCTION_DATABASE));
				} else {
					container.put(new RedisDatasource(RedisDatasource.DatabaseNumber.TEST_DATABASE));
				}
				container.put(new CacheDAOImpl());
				container.put(new NodeUsersDAOImpl());
			}
			container.put(new NodeUsersServiceImpl());
			container.put(new MasterAuthService());
			container.put(new CacheServiceImpl());

			SimpleLogger logger = container.get(SimpleLogger.class);
			logger.log("Container was successfully initialized");
		} catch (IOException | RuntimeException e) {
			throw new ContainerStrategyException(e);
		}
	}

}
