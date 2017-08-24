package ru.babobka.nodebusiness.service;

import java.io.Serializable;

public interface CacheService {

    void put(String key, Serializable content);

    <T extends Serializable> T get(String key);

}
