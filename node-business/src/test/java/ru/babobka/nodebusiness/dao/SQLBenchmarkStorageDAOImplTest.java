package ru.babobka.nodebusiness.dao;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodebusiness.model.Benchmark;
import ru.babobka.nodeutils.container.Container;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Arrays;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;

/**
 * Created by 123 on 31.03.2018.
 */
public class SQLBenchmarkStorageDAOImplTest {
    private SQLBenchmarkStorageDAOImpl sqlBenchmarkStorageDAO;
    private DataSource dataSource;


    @Before
    public void setUp() {
        dataSource = mock(DataSource.class);
        Container.getInstance().put(container -> {
            container.put(dataSource);
        });
        sqlBenchmarkStorageDAO = spy(new SQLBenchmarkStorageDAOImpl());
    }

    @Test
    public void testRemoveException() throws SQLException {
        String id = "123";
        QueryRunner runner = mock(QueryRunner.class);
        doReturn(runner).when(sqlBenchmarkStorageDAO).createQueryRunner(dataSource);
        when(runner.update(anyString(), eq(id))).thenThrow(new SQLException());
        assertFalse(sqlBenchmarkStorageDAO.remove(id));
    }


    @Test
    public void testRemoveNoRemoved() throws SQLException {
        String id = "123";
        QueryRunner runner = mock(QueryRunner.class);
        doReturn(runner).when(sqlBenchmarkStorageDAO).createQueryRunner(dataSource);
        when(runner.update(anyString(), eq(id))).thenReturn(0);
        assertFalse(sqlBenchmarkStorageDAO.remove(id));
    }

    @Test
    public void testRemove() throws SQLException {
        String id = "123";
        QueryRunner runner = mock(QueryRunner.class);
        doReturn(runner).when(sqlBenchmarkStorageDAO).createQueryRunner(dataSource);
        when(runner.update(anyString(), eq(id))).thenReturn(1);
        assertTrue(sqlBenchmarkStorageDAO.remove(id));
    }

    @Test(expected = RuntimeException.class)
    public void testGetException() throws SQLException {
        String id = "123";
        QueryRunner runner = mock(QueryRunner.class);
        doReturn(runner).when(sqlBenchmarkStorageDAO).createQueryRunner(dataSource);
        when(runner.query(anyString(), any(ResultSetHandler.class), eq(id))).thenThrow(new SQLException());
        sqlBenchmarkStorageDAO.get(id);
    }

    @Test
    public void testGet() throws SQLException {
        String id = "123";
        QueryRunner runner = mock(QueryRunner.class);
        Benchmark benchmark = new Benchmark();
        doReturn(runner).when(sqlBenchmarkStorageDAO).createQueryRunner(dataSource);
        when(runner.query(anyString(), any(ResultSetHandler.class), eq(id))).thenReturn(benchmark);
        assertEquals(benchmark, sqlBenchmarkStorageDAO.get(id));
    }

    @Test(expected = RuntimeException.class)
    public void testGetAllException() throws SQLException {
        QueryRunner runner = mock(QueryRunner.class);
        doReturn(runner).when(sqlBenchmarkStorageDAO).createQueryRunner(dataSource);
        when(runner.query(anyString(), any(ResultSetHandler.class))).thenThrow(new SQLException());
        sqlBenchmarkStorageDAO.getAll();
    }

    @Test
    public void testGetAllNoResult() throws SQLException {
        QueryRunner runner = mock(QueryRunner.class);
        doReturn(runner).when(sqlBenchmarkStorageDAO).createQueryRunner(dataSource);
        when(runner.query(anyString(), any(ResultSetHandler.class))).thenReturn(null);
        assertTrue(sqlBenchmarkStorageDAO.getAll().isEmpty());
    }

    @Test
    public void testGetAll() throws SQLException {
        QueryRunner runner = mock(QueryRunner.class);
        doReturn(runner).when(sqlBenchmarkStorageDAO).createQueryRunner(dataSource);
        when(runner.query(anyString(), any(ResultSetHandler.class))).thenReturn(Arrays.asList(new Benchmark(), new Benchmark()));
        assertFalse(sqlBenchmarkStorageDAO.getAll().isEmpty());
    }

    @Test
    public void testInsert() throws SQLException {
        QueryRunner runner = mock(QueryRunner.class);
        doReturn(runner).when(sqlBenchmarkStorageDAO).createQueryRunner(dataSource);
        when(runner.update(anyString(), anyVararg())).thenReturn(0);
        assertTrue(sqlBenchmarkStorageDAO.insert(new Benchmark()));
    }

    @Test
    public void testInsertException() throws SQLException {
        QueryRunner runner = mock(QueryRunner.class);
        doReturn(runner).when(sqlBenchmarkStorageDAO).createQueryRunner(dataSource);
        when(runner.update(anyString(), anyVararg())).thenThrow(new SQLException());
        assertFalse(sqlBenchmarkStorageDAO.insert(new Benchmark()));
    }
}

