package ru.babobka.nodeweb.webcontroller;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class NodeHealthCheckWebController extends WebController {

    @Override
    public void onGet(HttpExchange httpExchange) throws IOException {
        sendOk(httpExchange);
    }
}
