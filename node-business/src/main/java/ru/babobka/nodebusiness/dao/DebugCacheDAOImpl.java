package ru.babobka.nodebusiness.dao;

import java.io.Serializable;
import java.util.Map;

public class DebugCacheDAOImpl implements CacheDAO {

    private final Map<String, Serializable> debugDataMap;

    public DebugCacheDAOImpl(Map<String, Serializable> debugDataMap) {
        this.debugDataMap = debugDataMap;
    }

    @Override
    public <T extends Serializable> T get(String key) {
        return (T) debugDataMap.get(key);
    }

    @Override
    public void put(String key, Serializable value) {
        debugDataMap.put(key, value);
    }

}
