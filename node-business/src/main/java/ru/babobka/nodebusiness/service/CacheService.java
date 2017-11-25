package ru.babobka.nodebusiness.service;

import java.io.Serializable;

public interface CacheService<K> {

    void put(K key, Serializable content);

    <T extends Serializable> T get(K key);

}
