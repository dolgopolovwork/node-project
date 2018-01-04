package ru.babobka.nodebusiness.cache;

import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 123 on 04.01.2018.
 */
public class SoftCache<K extends Serializable, V extends Serializable> {

    private SoftReference<Map<K, V>> cacheSoftRef = new SoftReference<>(new HashMap<>());

    public synchronized void put(K key, V value) {
        Map<K, V> map = cacheSoftRef.get();
        if (map == null) {
            map = new HashMap<>();
            cacheSoftRef = new SoftReference<>(map);
        }
        map.put(key, value);
    }

    public synchronized V get(K key) {
        Map<K, V> map = cacheSoftRef.get();
        if (map == null) {
            return null;
        }
        return map.get(key);
    }
}