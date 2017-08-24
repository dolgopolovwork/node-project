package ru.babobka.nodetask.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ReducingResult implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -1442038023886275757L;

    private final Map<String, Serializable> map = new HashMap<>();

    public ReducingResult add(Map<String, Serializable> map) {
        if (map == null) {
            throw new IllegalArgumentException();
        }
        this.map.putAll(map);
        return this;
    }

    public ReducingResult add(String key, Serializable value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException();
        }
        this.map.put(key, value);
        return this;
    }

    public Serializable get(String key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        return this.map.get(key);
    }

    public Map<String, Serializable> map() {
        Map<String, Serializable> clone = new HashMap<>();
        clone.putAll(map);
        return clone;
    }
}
