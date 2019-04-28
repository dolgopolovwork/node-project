package ru.babobka.nodeweb.webcontroller;

import ru.babobka.vsjws.model.http.HttpRequest;
import ru.babobka.vsjws.model.http.HttpResponse;
import ru.babobka.vsjws.model.http.ResponseFactory;
import ru.babobka.vsjws.webcontroller.HttpWebController;

public class NodeHealthCheckWebController extends HttpWebController {

    public HttpResponse onGet(HttpRequest request) {
        return ResponseFactory.ok();
    }
}
