package ru.babobka.nodebusiness.dao;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import ru.babobka.nodebusiness.model.Benchmark;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.NodeLogger;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 123 on 16.03.2018.
 */
public class SQLBenchmarkStorageDAOImpl implements BenchmarkStorageDAO {

    private final DataSource dataSource = Container.getInstance().get(DataSource.class);
    private final NodeLogger nodeLogger = Container.getInstance().get(NodeLogger.class);

    @Override
    public boolean insert(Benchmark benchmark) {
        QueryRunner run = createQueryRunner(dataSource);
        try {
            run.update("INSERT INTO Benchmark (" +
                            "id, executionTime, startTime, description, appName, slaves, serviceThreads, os, user, processors, ramBytes, javaVersion" +
                            ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?)",
                    benchmark.getId(), benchmark.getExecutionTime(),
                    benchmark.getStartTime(), benchmark.getDescription(),
                    benchmark.getAppName(), benchmark.getSlaves(),
                    benchmark.getServiceThreads(), benchmark.getOs(),
                    benchmark.getUser(), benchmark.getProcessors(),
                    benchmark.getRamBytes(), benchmark.getJavaVersion());
            return true;
        } catch (SQLException e) {
            nodeLogger.error(e);
            return false;
        }
    }

    @Override
    public List<Benchmark> getAll() {
        QueryRunner run = createQueryRunner(dataSource);
        ResultSetHandler<List<Benchmark>> hander = new BeanListHandler<>(Benchmark.class);
        try {
            List<Benchmark> persons = run.query("SELECT * FROM Benchmark", hander);
            return persons == null ? new ArrayList<>() : persons;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Benchmark get(String id) {
        QueryRunner run = createQueryRunner(dataSource);
        ResultSetHandler<Benchmark> handler = new BeanHandler<>(Benchmark.class);
        try {
            return run.query("SELECT * FROM Benchmark WHERE id=?", handler, id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean remove(String id) {
        QueryRunner run = createQueryRunner(dataSource);
        try {
            int deleted = run.update("DELETE FROM benchmark WHERE ID=?", id);
            return deleted > 0;
        } catch (SQLException e) {
            nodeLogger.error(e);
            return false;
        }
    }

    QueryRunner createQueryRunner(DataSource dataSource) {
        return new QueryRunner(dataSource);
    }


}
