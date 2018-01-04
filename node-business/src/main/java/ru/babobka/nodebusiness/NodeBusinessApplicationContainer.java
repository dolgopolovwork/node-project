package ru.babobka.nodebusiness;

import ru.babobka.nodebusiness.cache.SoftCache;
import ru.babobka.nodebusiness.dao.DebugCacheDAOImpl;
import ru.babobka.nodebusiness.dao.DebugNodeUsersDAOImpl;
import ru.babobka.nodebusiness.mapper.UserDTOMapper;
import ru.babobka.nodebusiness.service.NodeUsersServiceImpl;
import ru.babobka.nodeutils.container.ApplicationContainer;
import ru.babobka.nodeutils.container.Container;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by 123 on 04.11.2017.
 */
public class NodeBusinessApplicationContainer implements ApplicationContainer {
    @Override
    public void contain(Container container) {
        container.put(new UserDTOMapper());
        container.put(new DebugNodeUsersDAOImpl(new HashMap<>()));
        container.put(new DebugCacheDAOImpl<Integer>(new SoftCache<>()));
        container.put(new NodeUsersServiceImpl());
    }
}
