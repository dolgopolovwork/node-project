package ru.babobka.nodebusiness;

import ru.babobka.nodebusiness.dao.SQLBenchmarkStorageDAOImpl;
import ru.babobka.nodebusiness.datasource.DataSourceConfig;
import ru.babobka.nodebusiness.datasource.PooledDataSourceFactory;
import ru.babobka.nodebusiness.service.BenchmarkStorageService;
import ru.babobka.nodeutils.container.ApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.container.ContainerException;
import ru.babobka.nodeutils.enums.Env;
import ru.babobka.nodeutils.util.JSONUtil;
import ru.babobka.nodeutils.util.StreamUtil;
import ru.babobka.nodeutils.util.TextUtil;

import java.io.IOException;

/**
 * Created by 123 on 18.03.2018.
 */
public class StorageApplicationContainer implements ApplicationContainer {


    @Override
    public void contain(Container container) {
        try {
            String storageConfigPath = TextUtil.getEnv(Env.NODE_STORAGE_CONFIG);
            if (storageConfigPath == null) {
                throw new IllegalStateException("environment variable " + Env.NODE_STORAGE_CONFIG + " was not set. can not read storage configs.");
            }
            container.putIfNotExists(new StreamUtil());
            DataSourceConfig dataSourceConfig = JSONUtil.readJsonFile(
                    container.get(StreamUtil.class),
                    storageConfigPath, DataSourceConfig.class);
            container.put(PooledDataSourceFactory.create(dataSourceConfig));
            container.put(new SQLBenchmarkStorageDAOImpl());
            container.put(new BenchmarkStorageService());
        } catch (IOException | ClassNotFoundException | RuntimeException e) {
            throw new ContainerException(e);
        }
    }
}
