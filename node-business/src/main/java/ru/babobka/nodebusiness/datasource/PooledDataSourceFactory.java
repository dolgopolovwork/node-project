package ru.babobka.nodebusiness.datasource;

import org.apache.commons.dbcp2.*;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

import javax.sql.DataSource;

/**
 * Created by 123 on 17.03.2018.
 */
public class PooledDataSourceFactory {

    public static DataSource create(DataSourceConfig config) throws ClassNotFoundException {
        Class.forName(config.getDriverName());
        ConnectionFactory connectionFactory =
                new DriverManagerConnectionFactory(config.getConnectURI(), null);
        PoolableConnectionFactory poolableConnectionFactory =
                new PoolableConnectionFactory(connectionFactory, null);
        ObjectPool<PoolableConnection> connectionPool =
                new GenericObjectPool<>(poolableConnectionFactory);
        poolableConnectionFactory.setPool(connectionPool);
        return new PoolingDataSource<>(connectionPool);
    }

}
