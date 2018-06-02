package ru.babobka.vsjws.model.http;

import ru.babobka.vsjws.enumerations.ResponseCode;
import ru.babobka.vsjws.model.Response;

import java.io.File;
import java.util.Map;

public class HttpResponse extends Response<byte[]> {

    private final byte[] content;
    private final File file;
    private final long contentLength;
    private ResponseCode responseCode;
    private static final byte[] EMPTY_ARRAY = {};

    HttpResponse(ResponseCode code, String contentType, byte[] content, File file, long contentLength) {
        setResponseCode(code);
        setContentType(contentType);
        if (content != null)
            this.content = content.clone();
        else
            this.content = null;
        this.file = file;
        this.contentLength = contentLength;
    }

    @Override
    public byte[] getContent() {
        if (content != null)
            return content.clone();
        return EMPTY_ARRAY;
    }

    public long getContentLength() {
        return contentLength;
    }

    public File getFile() {
        return file;
    }

    public HttpResponse addHeader(String key, String value) {
        if (isRestrictedHeader(key)) {
            throw new IllegalArgumentException(
                    "you cannot manually specify '" + key + "' header. It is restricted.");
        }
        if (key.endsWith(":")) {
            getHeaders().put(key, value);
        } else {
            getHeaders().put(key + ":", value);
        }
        return this;
    }

    public HttpResponse addHeader(String key, long value) {
        return addHeader(key, String.valueOf(value));
    }

    public HttpResponse addHeaders(Map<String, String> headers) {
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                addHeader(header.getKey(), header.getValue());
            }
        }
        return this;
    }

    public HttpResponse addCookie(String key, String value) {
        getCookies().put(key, value);
        return this;
    }

    public HttpResponse addCookies(Map<String, String> cookies) {
        if (cookies != null) {
            for (Map.Entry<String, String> cookie : cookies.entrySet()) {
                addCookie(cookie.getKey(), cookie.getValue());
            }
        }
        return this;
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }

    public HttpResponse setResponseCode(ResponseCode responseCode) {
        this.responseCode = responseCode;
        return this;
    }
}