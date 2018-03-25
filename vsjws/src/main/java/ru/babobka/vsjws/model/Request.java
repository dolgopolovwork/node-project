package ru.babobka.vsjws.model;

import ru.babobka.vsjws.enumerations.HttpMethod;
import ru.babobka.vsjws.model.http.HttpSession;
import ru.babobka.vsjws.util.HttpUtil;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.*;

public abstract class Request<B> {

    public static final String CONTENT_LENGTH_HEADER = "Content-Length";
    public static final String SESSION_ID_HEADER = "X-Session-Id";
    public static final String PROTOCOL = "HTTP/1.1";
    protected static final String HOST_HEADER = "Host";
    private final UUID id = UUID.randomUUID();
    private final List<Param> urlParams = new LinkedList<>();
    private final Map<String, String> cookies = new HashMap<>();
    private final Map<String, String> headers = new HashMap<>();
    private final HttpSession httpSession;
    private HttpMethod httpMethod;
    private String uri;
    private InetAddress address;

    public Request(HttpSession httpSession) {
        if (httpSession == null) {
            throw new IllegalArgumentException("httpSession is null");
        }
        this.httpSession = httpSession;
    }

    public void putSessionAttr(String key, Serializable value) {
        Map<String, Serializable> session = getSession();
        if (session != null) {
            session.put(key, value);
        }
    }

    public Serializable getSessionAttr(String key) {
        Map<String, Serializable> session = getSession();
        if (session != null) {
            return session.get(key);
        }
        return null;
    }

    private Map<String, Serializable> getSession() {
        String sessionId = getCookies().get(SESSION_ID_HEADER);
        if (sessionId == null) {
            return null;
        }
        return httpSession.getData(sessionId);
    }

    public Map<String, Serializable> getSessionCopy() {
        Map<String, Serializable> session = getSession();
        if (session != null) {
            return new HashMap<>(session);
        }
        return null;
    }

    protected abstract B getBody();

    public List<Param> getUrlParams() {
        List<Param> params = new LinkedList<>();
        params.addAll(urlParams);
        return params;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public String getUrlParam(String key) {
        return getParam(key, urlParams);
    }

    public List<String> getUrlParams(String key) {
        return getParams(key, urlParams);
    }

    public String getHeader(String key) {
        return headers.getOrDefault(key, "");
    }

    public String getUri() {
        return uri;
    }

    protected void setUri(String uri) {
        this.uri = uri;
        this.urlParams.addAll(HttpUtil.getUriParams(uri));
    }

    public UUID getId() {
        return id;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public InetAddress getAddress() {
        return address;
    }

    protected void setAddress(InetAddress address) {
        this.address = address;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public HttpMethod getMethod() {
        return httpMethod;
    }

    protected void setMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    protected String getParam(String key, List<Param> params) {
        for (Param param : params) {
            if (key.equals(param.getKey())) {
                return param.getValue();
            }
        }
        return "";
    }

    protected boolean isMethodWithContent(String method) {
        return method.equals(HttpMethod.PATCH.toString()) || method.equals(HttpMethod.POST.toString()) || method.equals(HttpMethod.PUT.toString());

    }

    protected List<String> getParams(String key, List<Param> params) {
        List<String> innerParams = new LinkedList<>();
        for (Param param : params) {
            if (key.equals(param.getKey())) {
                innerParams.add(param.getValue());
            }
        }
        return innerParams;
    }
}
