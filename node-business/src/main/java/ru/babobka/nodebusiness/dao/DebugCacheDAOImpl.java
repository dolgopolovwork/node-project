package ru.babobka.nodebusiness.dao;

import java.io.Serializable;
import java.util.Map;

public class DebugCacheDAOImpl<K extends Serializable> implements CacheDAO<K> {

    private final Map<K, Serializable> debugDataMap;

    public DebugCacheDAOImpl(Map<K, Serializable> debugDataMap) {
        this.debugDataMap = debugDataMap;
    }

    @Override
    public <T extends Serializable> T get(K key) {
        return (T) debugDataMap.get(key);
    }

    @Override
    public void put(K key, Serializable value) {
        debugDataMap.put(key, value);
    }

}
