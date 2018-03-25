package ru.babobka.nodebusiness.service;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.babobka.nodebusiness.StorageApplicationContainer;
import ru.babobka.nodebusiness.model.Benchmark;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.util.StreamUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by 123 on 18.03.2018.
 */
public class BenchmarkStorageServiceITCase {
    private static BenchmarkStorageService benchmarkStorageService;
    private static List<String> insertedUUIDs = new ArrayList<>();

    @BeforeClass
    public static void setUp() {
        Container.getInstance().put(mock(SimpleLogger.class));
        Container.getInstance().put(new StreamUtil());
        Container.getInstance().put(new StorageApplicationContainer());
        benchmarkStorageService = Container.getInstance().get(BenchmarkStorageService.class);
    }

    @AfterClass
    public static void tearDown() {
        for (String uuid : insertedUUIDs) {
            benchmarkStorageService.remove(uuid);
        }
    }

    @Test
    public void testInsert() {
        String uuid = UUID.randomUUID().toString();
        insertedUUIDs.add(uuid);
        assertTrue(benchmarkStorageService.insert(createBenchmark(uuid)));
    }

    @Test
    public void testGet() {
        String uuid = UUID.randomUUID().toString();
        insertedUUIDs.add(uuid);
        benchmarkStorageService.insert(createBenchmark(uuid));
        Benchmark benchmark = benchmarkStorageService.get(uuid);
        assertEquals(benchmark, createBenchmark(uuid));
    }

    @Test
    public void testRemove() {
        String uuid = UUID.randomUUID().toString();
        insertedUUIDs.add(uuid);
        benchmarkStorageService.insert(createBenchmark(uuid));
        assertTrue(benchmarkStorageService.remove(uuid));
    }

    @Test
    public void testGetAll() {
        List<Benchmark> benchmarks = benchmarkStorageService.getAll();
        assertFalse(benchmarks.isEmpty());
    }

    private Benchmark createBenchmark(String uuid) {
        Benchmark benchmark = new Benchmark();
        benchmark.setId(uuid);
        benchmark.setStartTime(0);
        benchmark.setExecutionTime(0);
        benchmark.setDescription("test description");
        benchmark.setAppName("test");
        benchmark.setSlaves(0);
        benchmark.setServiceThreads(0);
        benchmark.setCurrentSystemInfo();
        return benchmark;
    }
}
