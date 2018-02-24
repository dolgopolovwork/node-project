package ru.babobka.nodebusiness.service;

import org.junit.BeforeClass;
import org.junit.Test;
import ru.babobka.nodebusiness.dao.CacheDAO;
import ru.babobka.nodeutils.container.Container;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created by 123 on 13.08.2017.
 */
public class ResponseCacheServiceImplTest {

    private static final CacheDAO cacheDAO = mock(CacheDAO.class);
    private static ResponseCacheService responseCacheService;

    @BeforeClass
    public static void setUp() {
        Container.getInstance().put(cacheDAO);
        responseCacheService = new ResponseCacheService();
    }

    @Test
    public void testPut() {
        Integer key = 123;
        int value = 456;
        responseCacheService.put(key, value);
        verify(cacheDAO).put(key, value);
    }

    @Test
    public void testGet() {
        Integer key = 123;
        int value = 456;
        when(cacheDAO.get(key)).thenReturn(value);
        assertEquals((int) responseCacheService.get(key), value);
    }

}
