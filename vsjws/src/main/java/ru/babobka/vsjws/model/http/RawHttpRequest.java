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
    private HttpFirstLine firstLine;

    public RawHttpRequest(String firstLine, Map<String, String> headers, String body) {
        this.body = body;
        this.firstLine = new HttpFirstLine(firstLine);
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
                firstLine = new HttpFirstLine(line);
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

    public HttpFirstLine getFirstLine() {
        return firstLine;
    }

}
