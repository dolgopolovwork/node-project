package ru.babobka.vsjws.webcontroller;

import ru.babobka.vsjws.model.http.HttpResponse;
import ru.babobka.vsjws.model.http.ResponseFactory;
import ru.babobka.vsjws.model.json.JSONRequest;

import static ru.babobka.vsjws.enumerations.ResponseCode.NOT_IMPLEMENTED;

public class JSONWebController implements WebController<JSONRequest, HttpResponse> {

    @Override
    public HttpResponse onHead(JSONRequest request) {
        return ResponseFactory.code(NOT_IMPLEMENTED);
    }

    @Override
    public HttpResponse onGet(JSONRequest request) {
        return ResponseFactory.code(NOT_IMPLEMENTED);
    }

    @Override
    public HttpResponse onPost(JSONRequest request) {
        return ResponseFactory.code(NOT_IMPLEMENTED);
    }

    @Override
    public HttpResponse onPut(JSONRequest request) {
        return ResponseFactory.code(NOT_IMPLEMENTED);
    }

    @Override
    public HttpResponse onPatch(JSONRequest request) {
        return ResponseFactory.code(NOT_IMPLEMENTED);
    }

    @Override
    public HttpResponse onDelete(JSONRequest request) {
        return ResponseFactory.code(NOT_IMPLEMENTED);
    }

}
