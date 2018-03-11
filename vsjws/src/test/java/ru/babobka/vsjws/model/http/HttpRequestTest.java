package ru.babobka.vsjws.model.http;

import org.junit.BeforeClass;
import org.junit.Test;
import ru.babobka.vsjws.exception.BadProtocolSpecifiedException;
import ru.babobka.vsjws.exception.InvalidContentLengthException;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;

public class HttpRequestTest {

    private static RawHttpRequest badHttpProtocolRequest;
    private static RawHttpRequest noHeadersRequest;
    private static RawHttpRequest badMethodRequest;
    private static Map<String, String> normalHeaders;
    private static Map<String, String> badContentLengthHeaders;
    private static RawHttpRequest badContentLengthRequest;
    private static RawHttpRequest normalRequest;
    private HttpSession session = mock(HttpSession.class);

    @BeforeClass
    public static void init() {
        normalHeaders = new HashMap<>();
        normalHeaders.put("Host", "test");
        badContentLengthHeaders = new HashMap<>();
        badContentLengthHeaders.putAll(normalHeaders);
        badContentLengthHeaders.put("Content-Length", "-1");
        badHttpProtocolRequest = new RawHttpRequest("GET / HTTP/2", normalHeaders, null);
        noHeadersRequest = new RawHttpRequest("GET / HTTP/1.1", null, null);
        normalRequest = new RawHttpRequest(noHeadersRequest.getFirstLine().toString(), normalHeaders, null);
        badMethodRequest = new RawHttpRequest("GETS / HTTP/1.1", null, null);
        badContentLengthRequest = new RawHttpRequest("POST / HTTP/1.1",
                badContentLengthHeaders, null);
    }

    @Test(expected = BadProtocolSpecifiedException.class)
    public void testBadProtocolRequest() {
        new HttpRequest(session, null, badHttpProtocolRequest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoHeadersRequest() {
        new HttpRequest(session, null, noHeadersRequest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadMethodRequest() {
        new HttpRequest(session, null, badMethodRequest);
    }

    @Test(expected = InvalidContentLengthException.class)
    public void testBadContentLengthRequest() {
        new HttpRequest(session, null, badContentLengthRequest);
    }

    @Test
    public void testNormalRequest() {
        new HttpRequest(session, null, normalRequest);
    }

}
