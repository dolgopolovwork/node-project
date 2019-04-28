package ru.babobka.nodeweb.webcontroller;

import ru.babobka.nodebusiness.service.NodeMasterInfoService;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.vsjws.model.http.HttpRequest;
import ru.babobka.vsjws.model.http.HttpResponse;
import ru.babobka.vsjws.model.http.ResponseFactory;
import ru.babobka.vsjws.webcontroller.HttpWebController;

public class NodeClusterSizeWebController extends HttpWebController {

    private final NodeMasterInfoService nodeMasterInfoService =
            Container.getInstance().get(NodeMasterInfoService.class);

    @Override
    public HttpResponse onGet(HttpRequest request) {
        return ResponseFactory.text(nodeMasterInfoService.totalNodes());
    }
}
