package ru.babobka.nodebusiness.dao;

import java.io.Serializable;

/**
 * Created by 123 on 04.02.2018.
 */
public class H2CacheDAOImpl<K extends Serializable> implements CacheDAO<K> {
    @Override
    public <T extends Serializable> T get(K key) {
        return null;
    }

    @Override
    public void put(K key, Serializable value) {

    }
}
