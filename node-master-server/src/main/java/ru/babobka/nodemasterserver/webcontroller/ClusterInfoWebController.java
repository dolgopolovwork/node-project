package ru.babobka.nodemasterserver.webcontroller;

import ru.babobka.nodemasterserver.slave.SlavesStorage;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.vsjws.model.JSONRequest;
import ru.babobka.vsjws.model.JSONResponse;
import ru.babobka.vsjws.webcontroller.JSONWebController;

import java.io.Serializable;

public class ClusterInfoWebController extends JSONWebController {

    private final SlavesStorage slavesStorage = Container.getInstance().get(SlavesStorage.class);

    @Override
    public JSONResponse onGet(JSONRequest request) {
        return JSONResponse.ok((Serializable) slavesStorage.getCurrentClusterUserList());
    }

}
