package ru.babobka.nodebusiness.dao;

import ru.babobka.nodebusiness.cache.SoftCache;

import java.io.Serializable;

public class DebugCacheDAOImpl<K extends Serializable> implements CacheDAO<K> {

    private final SoftCache<K, Serializable> cache;

    public DebugCacheDAOImpl(SoftCache<K, Serializable> cache) {
        if (cache == null) {
            throw new IllegalArgumentException("cache is null");
        }
        this.cache = cache;
    }

    @Override
    public <T extends Serializable> T get(K key) {
        return (T) cache.get(key);
    }

    @Override
    public void put(K key, Serializable value) {
        cache.put(key, value);
    }
}
