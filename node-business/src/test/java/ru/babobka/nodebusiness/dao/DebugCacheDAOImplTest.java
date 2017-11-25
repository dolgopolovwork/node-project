package ru.babobka.nodebusiness.dao;

import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.*;

/**
 * Created by 123 on 10.08.2017.
 */
public class DebugCacheDAOImplTest {

    private CacheDAO<String> cacheDAO;

    @Before
    public void setUp() {
        Map<String, Serializable> map = new ConcurrentHashMap<>();
        map.put("abc", 123);
        cacheDAO = new DebugCacheDAOImpl<>(map);
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
