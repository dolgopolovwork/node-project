package ru.babobka.nodebusiness.dao;

import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodebusiness.cache.SoftCache;

import java.io.Serializable;

import static org.junit.Assert.*;

/**
 * Created by 123 on 10.08.2017.
 */
public class DebugCacheDAOImplTest {

    private CacheDAO<String> cacheDAO;

    @Before
    public void setUp() {
        SoftCache<String, Serializable> softCache = new SoftCache<>();
        softCache.put("abc", 123);
        cacheDAO = new DebugCacheDAOImpl<>(softCache);
    }

    @Test
    public void testGet() {
        assertEquals((int) cacheDAO.get("abc"), 123);
    }

    @Test
    public void testGetNotExistingKey() {
        assertNull(cacheDAO.get("xyz"));
    }

    @Test
    public void testPut() {
        String key = "xyz";
        cacheDAO.put(key, 456);
        assertNotNull(cacheDAO.get(key));
    }
}
