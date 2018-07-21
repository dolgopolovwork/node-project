package ru.babobka.nodeweb.webcontroller;

import ru.babobka.nodebusiness.service.NodeMasterInfoService;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.vsjws.enumerations.ResponseCode;
import ru.babobka.vsjws.model.http.HttpResponse;
import ru.babobka.vsjws.model.http.ResponseFactory;
import ru.babobka.vsjws.model.json.JSONRequest;
import ru.babobka.vsjws.webcontroller.JSONWebController;

/**
 * Created by 123 on 22.07.2018.
 */
public class NodeInfoWebController extends JSONWebController {
    private final NodeMasterInfoService nodeMasterInfoService = Container.getInstance().get(NodeMasterInfoService.class);

    @Override
    public HttpResponse onGet(JSONRequest request) {
        String infoType = request.getUrlParam("infoType");
        if ("clusterSize".equals(infoType)) {
            String taskName = request.getUrlParam("taskName");
            if (taskName.isEmpty()) {
                return ResponseFactory.json(nodeMasterInfoService.totalNodes());
            }
            return ResponseFactory.json(nodeMasterInfoService.totalNodes(taskName));
        }
        return ResponseFactory.code(ResponseCode.BAD_REQUEST);
    }
}
