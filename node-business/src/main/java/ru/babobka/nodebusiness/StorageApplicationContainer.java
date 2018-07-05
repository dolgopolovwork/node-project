package ru.babobka.nodebusiness;

import ru.babobka.nodebusiness.dao.SQLBenchmarkStorageDAOImpl;
import ru.babobka.nodebusiness.datasource.DataSourceConfig;
import ru.babobka.nodebusiness.datasource.PooledDataSourceFactory;
import ru.babobka.nodebusiness.service.BenchmarkStorageService;
import ru.babobka.nodeutils.container.AbstractApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.enums.Env;
import ru.babobka.nodeutils.util.JSONUtil;
import ru.babobka.nodeutils.util.StreamUtil;
import ru.babobka.nodeutils.util.TextUtil;

/**
 * Created by 123 on 18.03.2018.
 */
public class StorageApplicationContainer extends AbstractApplicationContainer {


    @Override
    protected void containImpl(Container container) throws Exception{

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
    }
}
