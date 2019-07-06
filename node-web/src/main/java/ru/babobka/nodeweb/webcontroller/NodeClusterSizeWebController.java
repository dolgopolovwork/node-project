package ru.babobka.nodeweb.webcontroller;

import com.sun.net.httpserver.HttpExchange;
import ru.babobka.nodebusiness.service.NodeMasterInfoService;
import ru.babobka.nodeutils.container.Container;

import java.io.IOException;

public class NodeClusterSizeWebController extends WebController {

    private final NodeMasterInfoService nodeMasterInfoService =
            Container.getInstance().get(NodeMasterInfoService.class);

    @Override
    protected void onGet(HttpExchange httpExchange) throws IOException {
        sendText(httpExchange, nodeMasterInfoService.totalNodes());
    }
}
