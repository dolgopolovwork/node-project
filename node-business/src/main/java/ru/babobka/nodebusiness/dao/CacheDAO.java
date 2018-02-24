package ru.babobka.nodebusiness.dao;

import java.io.Closeable;
import java.io.Serializable;

public interface CacheDAO extends Closeable {

    <T extends Serializable> T get(Integer key);

    void put(Integer key, Serializable value);

    void clear();

}
