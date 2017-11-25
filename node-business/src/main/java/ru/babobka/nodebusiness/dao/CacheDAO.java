package ru.babobka.nodebusiness.dao;

import java.io.Serializable;

public interface CacheDAO<K extends Serializable> {

    <T extends Serializable> T get(K key);

    void put(K key, Serializable value);

}
