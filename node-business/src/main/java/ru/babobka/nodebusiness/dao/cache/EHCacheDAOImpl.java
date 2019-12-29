package ru.babobka.nodebusiness.dao.cache;


import org.ehcache.Cache;
import org.ehcache.CacheManager;
import ru.babobka.nodebusiness.dao.cache.CacheDAO;
import ru.babobka.nodeutils.container.Container;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by 123 on 17.02.2018.
 */
public class EHCacheDAOImpl implements CacheDAO {

    private final CacheManager cacheManager = Container.getInstance().get(CacheManager.class);
    private final Cache<Integer, Serializable> responseCache = cacheManager.getCache("responseCache", Integer.class, Serializable.class);

    @Override
    public <T extends Serializable> T get(Integer key) {
        return (T) responseCache.get(key);
    }

    @Override
    public void put(Integer key, Serializable value) {
        responseCache.put(key, value);
    }

    @Override
    public void clear() {
        responseCache.clear();
    }

    @Override
    public void close() throws IOException {
        cacheManager.close();
    }
}
