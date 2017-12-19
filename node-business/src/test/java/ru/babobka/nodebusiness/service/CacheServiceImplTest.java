package ru.babobka.nodebusiness.service;

import org.junit.BeforeClass;
import org.junit.Test;
import ru.babobka.nodebusiness.dao.CacheDAO;
import ru.babobka.nodeutils.container.ApplicationContainer;
import ru.babobka.nodeutils.container.Container;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created by 123 on 13.08.2017.
 */
public class CacheServiceImplTest {

    private static final CacheDAO cacheDAO = mock(CacheDAO.class);
    private static CacheService cacheService;

    @BeforeClass
    public static void setUp() {
        new ApplicationContainer() {
            @Override
            public void contain(Container container) {
                container.put(cacheDAO);
            }
        }.contain(Container.getInstance());
        cacheService = new CacheServiceImpl();
    }

    @Test
    public void testPut() {
        String key = "abc";
        int value = 123;
        cacheService.put(key, value);
        verify(cacheDAO).put(key, value);
    }

    @Test
    public void testGet() {
        String key = "abc";
        int value = 123;
        when(cacheDAO.get(key)).thenReturn(value);
        assertEquals((int) cacheService.get(key), value);
    }

}
