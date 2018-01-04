package ru.babobka.nodemasterserver.webcontroller;

import ru.babobka.nodemasterserver.slave.SlavesStorage;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.vsjws.model.http.HttpResponse;
import ru.babobka.vsjws.model.http.ResponseFactory;
import ru.babobka.vsjws.model.json.JSONRequest;
import ru.babobka.vsjws.webcontroller.JSONWebController;

public class ClusterInfoWebController extends JSONWebController {

    private final SlavesStorage slavesStorage = Container.getInstance().get(SlavesStorage.class);

    @Override
    public HttpResponse onGet(JSONRequest request) {
        return ResponseFactory.json(slavesStorage.getCurrentClusterUserList());
    }

}
