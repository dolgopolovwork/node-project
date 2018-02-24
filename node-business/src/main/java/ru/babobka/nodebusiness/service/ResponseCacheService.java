package ru.babobka.nodebusiness.service;

import ru.babobka.nodebusiness.dao.CacheDAO;
import ru.babobka.nodeutils.container.Container;

import java.io.Serializable;

public class ResponseCacheService {

    private final CacheDAO cacheDAO = Container.getInstance().get(CacheDAO.class);

    public void put(Integer key, Serializable content) {
        cacheDAO.put(key, content);
    }

    public <T extends Serializable> T get(Integer key) {
        return cacheDAO.get(key);
    }
}
