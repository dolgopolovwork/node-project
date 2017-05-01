package ru.babobka.nodemasterserver.webcontroller;

import ru.babobka.nodemasterserver.slave.SlavesStorage;
import ru.babobka.nodeutils.container.Container;

import ru.babobka.vsjws.webcontroller.JSONWebController;
import ru.babobka.vsjws.webserver.JSONRequest;
import ru.babobka.vsjws.webserver.JSONResponse;

import java.io.Serializable;

public class ClusterInfoWebController extends JSONWebController {

    private final SlavesStorage slavesStorage = Container.getInstance().get(SlavesStorage.class);

    @Override
    public JSONResponse onGet(JSONRequest request) {
	return JSONResponse.ok((Serializable) slavesStorage.getCurrentClusterUserList());
    }

}
