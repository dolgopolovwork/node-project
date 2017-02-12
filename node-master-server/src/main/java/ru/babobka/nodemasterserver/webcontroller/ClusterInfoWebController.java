package ru.babobka.nodemasterserver.webcontroller;

import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodemasterserver.slave.SlavesStorage;
import ru.babobka.vsjws.model.HttpRequest;
import ru.babobka.vsjws.model.HttpResponse;
import ru.babobka.vsjws.webcontroller.WebController;

public class ClusterInfoWebController extends WebController {

    private final SlavesStorage slavesStorage = Container.getInstance().get(SlavesStorage.class);

    @Override
    public HttpResponse onGet(HttpRequest request) {
	return HttpResponse.jsonResponse(slavesStorage.getCurrentClusterUserList());
    }

}
