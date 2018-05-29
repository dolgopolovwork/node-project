package ru.babobka.vsjws.util;

import ru.babobka.vsjws.enumerations.RestrictedHeader;
import ru.babobka.vsjws.model.Param;
import ru.babobka.vsjws.model.http.HttpRequest;
import ru.babobka.vsjws.model.http.HttpResponse;
import ru.babobka.vsjws.model.http.ResponseFactory;

import java.io.*;
import java.net.URLDecoder;
import java.util.*;

/**
 * Created by dolgopolov.a on 29.12.15.
 */
public interface HttpUtil {

    static void writeResponse(OutputStream os, HttpResponse response, boolean noContent) throws IOException {
        if (response == null) {
            throw new IllegalArgumentException("response is null");
        }
        os.write(getHeadersString(response).getBytes(ResponseFactory.MAIN_ENCODING));
        if (noContent) {
            os.flush();
            return;
        } else if (response.getFile() == null) {
            os.write(response.getContent());
            return;
        }
        byte[] buf = new byte[8192];
        int c;
        try (InputStream is = new FileInputStream(response.getFile())) {
            while ((c = is.read(buf, 0, buf.length)) > 0) {
                os.write(buf, 0, c);
                os.flush();
            }
        }
        os.flush();
    }

    static Map<String, String> getHeaders(HttpResponse response) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put(RestrictedHeader.SERVER + ":", "vsjws");
        headers.put(RestrictedHeader.CONTENT_TYPE + ":", response.getContentType());
        headers.put(RestrictedHeader.CONTENT_LENGTH + ":",
                String.valueOf(response.getContentLength()));
        headers.put(RestrictedHeader.CONNECTION + ":", "close");
        headers.putAll(response.getHttpCookieHeaders());
        headers.putAll(response.getHeaders());
        return headers;
    }

    static String getHeadersString(HttpResponse response) {
        Map<String, String> headers = getHeaders(response);
        StringBuilder headerBuilder = new StringBuilder(
                HttpRequest.PROTOCOL + " " + response.getResponseCode() + "\n");
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            headerBuilder.append(entry.getKey());
            headerBuilder.append(" ");
            headerBuilder.append(entry.getValue());
            headerBuilder.append("\r\n");
        }
        headerBuilder.append("\r\n");
        return headerBuilder.toString();
    }

    static Set<Param> getUriParams(String uri) {
        String[] uriArray = uri.split("\\?");
        if (uriArray.length > 1) {
            return getParams(uriArray[1]);
        } else {
            return new HashSet<>();
        }
    }

    static String getHeaderValue(String headerLine) {
        return headerLine.substring(headerLine.indexOf(':') + 1, headerLine.length()).trim();
    }

    static String cleanUri(String uri) {
        return uri.split("\\?")[0];
    }

    static String readBody(int contentLength, BufferedReader br) throws IOException {
        StringBuilder body = new StringBuilder();
        for (int i = 0; i < contentLength; i++) {
            body.append((char) br.read());
        }
        return URLDecoder.decode(body.toString(), ResponseFactory.MAIN_ENCODING.name());
    }

    static Map<String, String> getCookies(String cookiesLine) {
        Map<String, String> cookies = new HashMap<>();
        if (cookiesLine.isEmpty()) {
            return cookies;
        }
        String[] cookiesArray = cookiesLine.substring(0, cookiesLine.length()).split("; ");
        for (String cookie : cookiesArray) {
            String[] cookieHeader = cookie.split("=");
            cookies.put(cookieHeader[0], cookieHeader[1]);
        }
        return cookies;
    }

    static String generateSessionId() {
        return UUID.randomUUID().toString();
    }

    static Set<Param> getParams(String paramText) {
        if (paramText == null || paramText.isEmpty()) {
            return new LinkedHashSet<>();
        }
        Set<Param> params = new LinkedHashSet<>();
        String[] paramsArray = paramText.split("&");
        for (String param : paramsArray) {
            String[] keyValue = param.split("=");
            if (keyValue.length > 1) {
                params.add(new Param(keyValue[0], keyValue[1]));
            }
        }
        return params;
    }

}
