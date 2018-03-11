package ru.babobka.vsjws.model;

import ru.babobka.vsjws.enumerations.RestrictedHeader;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by 123 on 22.04.2017.
 */
public abstract class Response<T extends Serializable> {

    private final UUID id = UUID.randomUUID();

    private final Map<String, String> headers = new LinkedHashMap<>();

    private final Map<String, String> cookies = new HashMap<>();

    private String contentType;

    protected static boolean isRestrictedHeader(String headerKey) {
        for (RestrictedHeader header : RestrictedHeader.values()) {
            if (header.toString().equals(headerKey)) {
                return true;
            }
        }
        return false;
    }

    public Map<String, String> getHttpCookieHeaders() {
        Map<String, String> headers = new HashMap<>();
        for (Map.Entry<String, String> cookie : cookies.entrySet()) {
            headers.put("Set-Cookie:", cookie.getKey() + "=" + cookie.getValue());
        }
        return headers;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public String getContentType() {
        return contentType;
    }

    protected void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public UUID getId() {
        return id;
    }

    protected abstract T getContent();
}
