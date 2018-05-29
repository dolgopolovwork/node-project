package ru.babobka.vsjws.mapper;

import ru.babobka.nodeutils.util.JSONUtil;
import ru.babobka.vsjws.model.http.HttpRequest;
import ru.babobka.vsjws.model.http.session.HttpSession;
import ru.babobka.vsjws.model.json.JSONRequest;

public class JSONRequestMapper {
    public JSONRequest map(HttpSession session, HttpRequest request) {
        return new JSONRequest(session,
                request.getUri(),
                request.getUrlParams(),
                request.getCookies(),
                request.getHeaders(),
                request.getAddress(),
                JSONUtil.toJsonDefault(request.getBody()),
                request.getMethod());
    }
}
