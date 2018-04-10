package ru.babobka.vsjws.model.http;

import ru.babobka.vsjws.enumerations.HttpMethod;
import ru.babobka.vsjws.model.Param;
import ru.babobka.vsjws.model.Request;
import ru.babobka.vsjws.util.HttpUtil;

import java.net.InetAddress;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by dolgopolov.a on 30.12.15.
 */
public class HttpRequest extends Request<String> {
    public static final String PROTOCOL = "HTTP/1.1";
    private final String body;

    private final List<Param> formParams = new LinkedList<>();

    public HttpRequest(HttpSession httpSession, InetAddress address, RawHttpRequest rawHttpRequest) {
        super(httpSession);
        RawHttpRequest.FirstLine firstLine = rawHttpRequest.getFirstLine();
        String method = firstLine.getMethod();
        getHeaders().putAll(rawHttpRequest.getHeaders());
        setUri(firstLine.getUri());
        getCookies().putAll(HttpUtil.getCookies(rawHttpRequest.getHeaders().getOrDefault("Cookie", "")));
        this.body = rawHttpRequest.getBody();
        this.formParams.addAll(HttpUtil.getParams(body));
        getUrlParams().addAll(HttpUtil.getUriParams(getUri()));
        setAddress(address);
        setMethod(HttpMethod.valueOf(method));
    }

    public String getFormParam(String key) {
        return getParam(key, formParams);
    }

    public List<String> getFormParams(String key) {
        return getParams(key, formParams);
    }

    public List<Param> getFormParams() {
        List<Param> params = new LinkedList<>();
        params.addAll(formParams);
        return params;
    }

    @Override
    public String getBody() {
        return body;
    }

}
