package ru.babobka.vsjws.model.http;

import ru.babobka.nodeutils.util.TextUtil;
import ru.babobka.vsjws.model.Header;
import ru.babobka.vsjws.util.HttpUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class RawHttpRequest {

    private final Map<String, String> headers = new HashMap<>();

    private String body;

    private FirstLine firstLine;

    public RawHttpRequest(String firstLine, Map<String, String> headers, String body) {
        super();
        this.body = body;
        this.firstLine = new FirstLine(firstLine);
        if (headers != null)
            this.headers.putAll(headers);
    }

    public RawHttpRequest(InputStream is) throws IOException {
        int row = 0;
        BufferedReader br = new BufferedReader(new InputStreamReader(is, ResponseFactory.MAIN_ENCODING));
        String line;
        while ((line = br.readLine()) != null) {
            if (line.isEmpty()) {
                int contentLength = TextUtil.tryParseInt(headers.get(HttpRequest.CONTENT_LENGTH_HEADER));
                this.body = HttpUtil.readBody(contentLength, br);
                break;
            }
            line = URLDecoder.decode(line, ResponseFactory.MAIN_ENCODING.name());
            if (row == 0) {
                firstLine = new FirstLine(line);
            } else {
                Header header = new Header(line);
                headers.put(header.getKey(), header.getValue());
            }
            row++;
        }
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public FirstLine getFirstLine() {
        return firstLine;
    }

    @Override
    public String toString() {
        return "RawHttpRequest [headers=" + headers + ", body=" + body + ", firstLine=" + firstLine + "]";
    }

    public static class FirstLine {
        private final String method;

        private final String uri;

        private final String protocol;

        public FirstLine(String line) {
            if (line.length() < 3) {
                throw new IllegalArgumentException("Bad first line");
            }
            try {
                method = line.substring(0, line.indexOf(' '));
                protocol = line.substring(line.lastIndexOf(' '), line.length()).trim();
                uri = line.substring(method.length(), line.length() - protocol.length()).trim();
            } catch (RuntimeException e) {
                throw new IllegalArgumentException("cannot parse first line");
            }
        }

        public String getMethod() {
            return method;
        }

        public String getUri() {
            return uri;
        }

        public String getProtocol() {
            return protocol;
        }

        @Override
        public String toString() {
            return method + " " + uri + " " + protocol;
        }

    }

}
