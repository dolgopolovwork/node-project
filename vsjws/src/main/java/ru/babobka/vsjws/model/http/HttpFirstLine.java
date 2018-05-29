package ru.babobka.vsjws.model.http;

/**
 * Created by 123 on 27.05.2018.
 */
public class HttpFirstLine {
    private final String method;
    private final String uri;
    private final String protocol;

    public HttpFirstLine(String line) {
        if (line.length() < 3) {
            throw new IllegalArgumentException("bad first line");
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
