package ru.babobka.nodebusiness.service;


import ru.babobka.nodebusiness.dao.CacheDAO;
import ru.babobka.nodeutils.container.Container;

import java.io.Serializable;


public class CacheServiceImpl<K extends Serializable> implements CacheService<K> {

    private final CacheDAO<K> cacheDAO = Container.getInstance().get(CacheDAO.class);

    @Override
    public void put(K key, Serializable content) {
        cacheDAO.put(key, content);
    }

    @Override
    public <T extends Serializable> T get(K key) {
        return cacheDAO.get(key);
    }

}
