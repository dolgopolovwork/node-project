package ru.babobka.vsjws.model.json;

import org.json.JSONObject;
import ru.babobka.nodeutils.util.JSONUtil;
import ru.babobka.vsjws.enumerations.HttpMethod;
import ru.babobka.vsjws.model.Param;
import ru.babobka.vsjws.model.Request;
import ru.babobka.vsjws.model.http.HttpSession;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;

public class JSONRequest extends Request<JSONObject> {
    private final JSONObject json;

    public JSONRequest(HttpSession httpSession, String uri, List<Param> urlParams, Map<String, String> cookies, Map<String, String> headers,
                       InetAddress address, JSONObject json, HttpMethod method) {
        super(httpSession);
        this.setMethod(method);
        this.setUri(uri);
        this.getUrlParams().addAll(urlParams);
        this.getCookies().putAll(cookies);
        this.getHeaders().putAll(headers);
        this.setAddress(address);
        this.json = json;
    }

    @Override
    public JSONObject getBody() {
        return json;
    }

    public <T> T getBody(Class<T> t) {
        return JSONUtil.parseJson(json, t);
    }

}
