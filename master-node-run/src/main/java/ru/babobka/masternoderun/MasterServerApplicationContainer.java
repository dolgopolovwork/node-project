package ru.babobka.masternoderun;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import ru.babobka.nodebusiness.NodeBusinessApplicationContainer;
import ru.babobka.nodebusiness.dao.user.DBNodeUserDAOImpl;
import ru.babobka.nodebusiness.model.User;
import ru.babobka.nodeconfigs.master.MasterServerConfig;
import ru.babobka.nodemasterserver.server.MasterServerApplicationSubContainer;
import ru.babobka.nodesecurity.SecurityApplicationContainer;
import ru.babobka.nodetask.NodeTaskApplicationContainer;
import ru.babobka.nodeutils.NodeUtilsApplicationContainer;
import ru.babobka.nodeutils.container.AbstractApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.network.NodeConnectionFactory;
import ru.babobka.nodeweb.NodeMasterWebApplicationContainer;

import java.util.Properties;

/**
 * Created by 123 on 05.11.2017.
 */
public class MasterServerApplicationContainer extends AbstractApplicationContainer {

    @Override
    protected void containImpl(Container container) {
        MasterServerConfig config = container.get(MasterServerConfig.class);
        container.put(createSessionFactory(config));
        container.put(new DBNodeUserDAOImpl());
        container.put(new SecurityApplicationContainer());
        container.put(new NodeConnectionFactory());
        container.put(new NodeUtilsApplicationContainer());
        container.put(new NodeTaskApplicationContainer());
        container.put(new NodeBusinessApplicationContainer());
        container.put(new NodeMasterWebApplicationContainer());
        container.put(new MasterServerApplicationSubContainer(config));
    }

    public static SessionFactory createSessionFactory(MasterServerConfig masterServerConfig) {
        try {
            Properties properties = new Properties();
            properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
            properties.put("hibernate.connection.driver_class", "org.postgresql.Driver");
            properties.put("hibernate.connection.url", "jdbc:postgresql://" + masterServerConfig.getDbConfig().getHost() + ":" + masterServerConfig.getDbConfig().getPort() + "/" + masterServerConfig.getDbConfig().getUser());
            properties.put("hibernate.connection.username", masterServerConfig.getDbConfig().getUser());
            properties.put("hibernate.connection.password", masterServerConfig.getDbConfig().getPassword());
            properties.put("show_sql", "true");
            properties.put("format_sql", "true");
            properties.put(Environment.C3P0_MIN_SIZE, 5);
            properties.put(Environment.C3P0_MAX_SIZE, 20);
            properties.put(Environment.C3P0_TIMEOUT, 1800);
            Configuration configuration = new Configuration();
            configuration.addAnnotatedClass(User.class);
            configuration.setProperties(properties);
            return configuration.buildSessionFactory();
        } catch (Throwable th) {
            throw new ExceptionInInitializerError(th);
        }
    }
}
