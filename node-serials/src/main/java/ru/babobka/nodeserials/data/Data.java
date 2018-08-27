package ru.babobka.nodeserials.data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by 123 on 28.05.2018.
 */
public class Data implements Serializable {
    private static final long serialVersionUID = 5784960116404504151L;
    private final Map<String, Serializable> dataCollection = new TreeMap<>();

    public Data put(String key, int value) {
        dataCollection.put(key, value);
        return this;
    }

    public Data put(String key, long value) {
        dataCollection.put(key, value);
        return this;
    }

    public Data put(String key, byte value) {
        dataCollection.put(key, value);
        return this;
    }

    public Data put(String key, short value) {
        dataCollection.put(key, value);
        return this;
    }

    public Data put(String key, boolean value) {
        dataCollection.put(key, value);
        return this;
    }

    public Data put(String key, char value) {
        dataCollection.put(key, value);
        return this;
    }

    public Data put(String key, float value) {
        dataCollection.put(key, value);
        return this;
    }

    public Data put(String key, double value) {
        dataCollection.put(key, value);
        return this;
    }

    public Data put(String key, String value) {
        dataCollection.put(key, value);
        return this;
    }

    public Data put(String key, BigInteger value) {
        dataCollection.put(key, value);
        return this;
    }

    public Data put(String key, BigDecimal value) {
        dataCollection.put(key, value);
        return this;
    }

    public Data put(Data data) {
        if (data != null) {
            dataCollection.putAll(data.dataCollection);
        }
        return this;
    }

    public <T extends Serializable> T get(String key) {
        return (T) dataCollection.get(key);
    }

    public void remove(String key) {
        dataCollection.remove(key);
    }

    public Data putString(String key, List<String> value) {
        dataCollection.put(key, (Serializable) value);
        return this;
    }

    public FinalIterator<Map.Entry<String, Serializable>> getIterator() {
        return new FinalIterator<>(dataCollection.entrySet().iterator());
    }

    public boolean isEmpty() {
        return dataCollection.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Data data = (Data) o;

        return dataCollection.equals(data.dataCollection);
    }

    @Override
    public int hashCode() {
        return dataCollection.hashCode();
    }

    @Override
    public String toString() {
        return "Data{" +
                "dataCollection=" + dataCollection +
                '}';
    }
}
