package ru.babobka.nodebusiness.dao;

import java.io.Serializable;

public interface CacheDAO {

    <T extends Serializable> T get(String key);

    void put(String key, Serializable value);

}
