package ru.babobka.nodebusiness;

import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import ru.babobka.nodebusiness.dao.cache.EHCacheDAOImpl;
import ru.babobka.nodebusiness.mapper.UserDTOMapper;
import ru.babobka.nodebusiness.service.NodeUsersServiceImpl;
import ru.babobka.nodebusiness.service.ResponseCacheService;
import ru.babobka.nodeutils.container.AbstractApplicationContainer;
import ru.babobka.nodeutils.container.Container;

import java.io.Serializable;

/**
 * Created by 123 on 04.11.2017.
 */
public class NodeBusinessApplicationContainer extends AbstractApplicationContainer {

    @Override
    protected void containImpl(Container container) {
        container.put(new UserDTOMapper());
        container.put(createCacheManager());
        container.put(new EHCacheDAOImpl());
        container.put(new NodeUsersServiceImpl());
        container.put(new ResponseCacheService());
    }

    private CacheManager createCacheManager() {
        return CacheManagerBuilder.newCacheManagerBuilder()
                .withCache("responseCache",
                        CacheConfigurationBuilder.newCacheConfigurationBuilder(Integer.class, Serializable.class,
                                ResourcePoolsBuilder.newResourcePoolsBuilder()
                                        .heap(100, EntryUnit.ENTRIES)
                                        .offheap(50, MemoryUnit.MB))).build(true);
    }
}
