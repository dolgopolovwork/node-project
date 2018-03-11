package ru.babobka.vsjws.webcontroller;

import ru.babobka.nodeutils.util.JSONUtil;
import ru.babobka.nodeutils.util.TextUtil;
import ru.babobka.vsjws.enumerations.ResponseCode;
import ru.babobka.vsjws.model.FilterResponse;
import ru.babobka.vsjws.model.http.HttpRequest;
import ru.babobka.vsjws.model.http.HttpResponse;
import ru.babobka.vsjws.model.http.ResponseFactory;

public class JSONWebFilter implements WebFilter {

    @Override
    public void afterFilter(HttpRequest request, HttpResponse response) {

    }

    @Override
    public FilterResponse onFilter(HttpRequest request) {
        String body = request.getBody();
        if (!TextUtil.isEmpty(body) && !JSONUtil.isJSONValid(body)) {
            return FilterResponse
                    .failed(ResponseFactory.text("Invalid JSON").setResponseCode(ResponseCode.BAD_REQUEST));
        }
        return FilterResponse.proceed();
    }
}
