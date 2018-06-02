package ru.babobka.vsjws.mapper;

import ru.babobka.vsjws.model.http.HttpRequest;
import ru.babobka.vsjws.model.http.HttpResponse;
import ru.babobka.vsjws.model.http.session.HttpSession;
import ru.babobka.vsjws.webcontroller.HttpWebController;
import ru.babobka.vsjws.webcontroller.JSONWebController;
import ru.babobka.vsjws.webcontroller.JSONWebFilter;

public class JSONWebControllerMapper {

    private final JSONRequestMapper requestMapper = new JSONRequestMapper();

    private final JSONWebFilter jsonWEbFilter = new JSONWebFilter();

    public HttpWebController map(HttpSession httpSession, JSONWebController controller) {
        return new HttpWebController() {
            @Override
            public HttpResponse onGet(HttpRequest request) {
                return controller.onGet(requestMapper.map(httpSession, request));
            }

            @Override
            public HttpResponse onPost(HttpRequest request) {
                return controller.onPost(requestMapper.map(httpSession, request));
            }

            @Override
            public HttpResponse onPut(HttpRequest request) {
                return controller.onPut(requestMapper.map(httpSession, request));
            }

            @Override
            public HttpResponse onPatch(HttpRequest request) {
                return controller.onPatch(requestMapper.map(httpSession, request));
            }

            @Override
            public HttpResponse onDelete(HttpRequest request) {
                return controller.onDelete(requestMapper.map(httpSession, request));
            }

            @Override
            public HttpResponse onHead(HttpRequest request) {
                return controller.onHead(requestMapper.map(httpSession, request));
            }
        }.addWebFilter(jsonWEbFilter);
    }
}
