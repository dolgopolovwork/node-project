package ru.babobka.nodeweb.webcontroller;

import com.sun.net.httpserver.HttpExchange;
import lombok.NonNull;
import ru.babobka.nodeutils.util.JSONUtil;
import ru.babobka.nodeutils.util.TextUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Scanner;

public abstract class WebControllerUtil {

    private static final String UTF_8 = "UTF-8";
    private static final int NOT_ALLOWED_CODE = 405;
    private static final int OK_CODE = 200;
    private static final int BAD_REQUEST_CODE = 400;
    private static final int NOT_FOUND_CODE = 404;
    private static final int SERVER_ERROR = 500;

    protected static void notAllowed(HttpExchange httpExchange) throws IOException {
        byte[] bytes = "Method not allowed".getBytes(TextUtil.CHARSET);
        httpExchange.sendResponseHeaders(NOT_ALLOWED_CODE, bytes.length);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    protected static void sendOk(@NonNull HttpExchange httpExchange) throws IOException {
        sendText(httpExchange, "Ok", OK_CODE);
    }

    protected static void sendBadRequest(@NonNull HttpExchange httpExchange, @NonNull String errorMessage) throws IOException {
        sendText(httpExchange, errorMessage, BAD_REQUEST_CODE);
    }

    protected static void sendNotFound(@NonNull HttpExchange httpExchange, @NonNull String errorMessage) throws IOException {
        sendText(httpExchange, errorMessage, NOT_FOUND_CODE);
    }

    protected static void sendServerError(@NonNull HttpExchange httpExchange, @NonNull String errorMessage) throws IOException {
        sendText(httpExchange, errorMessage, SERVER_ERROR);
    }

    protected static void sendText(
            @NonNull HttpExchange httpExchange,
            @NonNull Object object) throws IOException {
        sendText(httpExchange, object, OK_CODE);
    }

    protected static void sendText(
            @NonNull HttpExchange httpExchange,
            @NonNull Object object,
            int statusCode) throws IOException {
        byte[] bytes = String.valueOf(object).getBytes(TextUtil.CHARSET);
        httpExchange.sendResponseHeaders(statusCode, bytes.length);
        httpExchange.setAttribute("Content-type", "text/plain");
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    protected static void sendText(
            @NonNull HttpExchange httpExchange,
            @NonNull String text,
            int statusCode) throws IOException {
        byte[] bytes = text.getBytes(TextUtil.CHARSET);
        httpExchange.sendResponseHeaders(statusCode, bytes.length);
        httpExchange.setAttribute("Content-type", "text/plain");
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    protected static void sendJson(
            @NonNull HttpExchange httpExchange,
            @NonNull Object object) throws IOException {
        byte[] bytes = JSONUtil.toJsonString(object).getBytes(TextUtil.CHARSET);
        httpExchange.sendResponseHeaders(OK_CODE, bytes.length);
        httpExchange.setAttribute("Content-type", "application/json");
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    protected static String getUriParam(@NonNull HttpExchange httpExchange, @NonNull String key) {
        try {
            String query = httpExchange.getRequestURI().getQuery();
            if (query == null) {
                return null;
            }
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                if (key.equals(URLDecoder.decode(pair.substring(0, idx), UTF_8))) {
                    return URLDecoder.decode(pair.substring(idx + 1), UTF_8);
                }
            }
            return null;
        } catch (UnsupportedEncodingException ignored) {
            return null;
        }
    }

    protected static <T> T readJson(@NonNull HttpExchange httpExchange, @NonNull Class<T> clazz) {
        return JSONUtil.parseJson(convertStreamToString(httpExchange.getRequestBody()), clazz);
    }

    private static String convertStreamToString(@NonNull InputStream is) {
        Scanner s = new Scanner(is, TextUtil.CHARSET.name()).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

}
