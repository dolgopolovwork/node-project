package ru.babobka.nodebusiness.dao;

import ru.babobka.nodebusiness.model.Benchmark;

import java.util.List;

/**
 * Created by 123 on 16.03.2018.
 */
public interface BenchmarkStorageDAO {

    boolean insert(Benchmark benchmark);

    List<Benchmark> getAll();

    Benchmark get(String id);

    boolean remove(String id);
}
