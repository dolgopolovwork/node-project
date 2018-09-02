package ru.babobka.vsjws.webserver;

import ru.babobka.nodeutils.container.AbstractApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.vsjws.mapper.JSONWebControllerMapper;
import ru.babobka.vsjws.validator.config.WebServerConfigValidator;
import ru.babobka.vsjws.validator.request.RequestValidator;

/**
 * Created by 123 on 22.07.2018.
 */
public class WebServerApplicationContainer extends AbstractApplicationContainer {
    @Override
    protected void containImpl(Container container) throws Exception {
        container.put(new RequestValidator());
        container.put(new WebServerConfigValidator());
        container.put(new JSONWebControllerMapper());
    }
}
