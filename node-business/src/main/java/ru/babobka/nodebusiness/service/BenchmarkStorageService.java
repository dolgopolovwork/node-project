package ru.babobka.nodebusiness.service;

import ru.babobka.nodebusiness.dao.BenchmarkStorageDAO;
import ru.babobka.nodebusiness.model.Benchmark;
import ru.babobka.nodeutils.container.Container;

import java.util.List;

/**
 * Created by 123 on 15.03.2018.
 */
public class BenchmarkStorageService {

    private final BenchmarkStorageDAO benchmarkStorageDAO = Container.getInstance().get(BenchmarkStorageDAO.class);

    public boolean insert(Benchmark benchmark) {
        if (benchmark == null) {
            throw new IllegalArgumentException("benchmark is null");
        }
        return benchmarkStorageDAO.insert(benchmark);
    }

    public List<Benchmark> getAll() {
        return benchmarkStorageDAO.getAll();
    }

    public Benchmark get(String id) {
        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }
        return benchmarkStorageDAO.get(id);
    }

    public boolean remove(String id) {
        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }
        return benchmarkStorageDAO.remove(id);
    }
}
