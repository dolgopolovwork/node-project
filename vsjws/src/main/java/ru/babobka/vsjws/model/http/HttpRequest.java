package ru.babobka.vsjws.model.http;

import ru.babobka.nodeutils.util.TextUtil;
import ru.babobka.vsjws.enumerations.HttpMethod;
import ru.babobka.vsjws.exception.BadProtocolSpecifiedException;
import ru.babobka.vsjws.exception.InvalidContentLengthException;
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

    private final String body;

    private final List<Param> formParams = new LinkedList<>();

    public HttpRequest(HttpSession httpSession, InetAddress address, RawHttpRequest rawHttpRequest) {
        super(httpSession);
        if (rawHttpRequest.getFirstLine() == null) {
            throw new IllegalArgumentException("First line is empty");
        } else if (!rawHttpRequest.getHeaders().containsKey(HOST_HEADER)) {
            throw new IllegalArgumentException("Header 'Host' was not set");
        }
        getHeaders().putAll(rawHttpRequest.getHeaders());
        RawHttpRequest.FirstLine firstLine = rawHttpRequest.getFirstLine();
        String method = firstLine.getMethod();
        int contentLength = TextUtil.tryParseInt(getHeaders().get(HttpRequest.CONTENT_LENGTH_HEADER), -1);
        if (method == null) {
            throw new IllegalArgumentException("HTTP method was not specified");
        } else if (!HttpMethod.isValidMethod(method)) {
            throw new IllegalArgumentException("HTTP method is invalid");
        } else if (isMethodWithContent(method) && contentLength == -1) {
            throw new InvalidContentLengthException("'Content-Length' header wasn't set properly");
        }
        setUri(firstLine.getUri());
        if (!firstLine.getProtocol().equals(PROTOCOL)) {
            throw new BadProtocolSpecifiedException();
        }
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
