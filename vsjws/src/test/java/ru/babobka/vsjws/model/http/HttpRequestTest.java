package ru.babobka.vsjws.model.http;

import org.junit.BeforeClass;
import org.junit.Test;
import ru.babobka.vsjws.model.http.session.HttpSession;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;

public class HttpRequestTest {


    private static RawHttpRequest noHeadersRequest;
    private static Map<String, String> normalHeaders;
    private static RawHttpRequest normalRequest;
    private HttpSession session = mock(HttpSession.class);

    @BeforeClass
    public static void init() {
        normalHeaders = new HashMap<>();
        normalHeaders.put("Host", "test");
        noHeadersRequest = new RawHttpRequest("GET / HTTP/1.1", null, null);
        normalRequest = new RawHttpRequest(noHeadersRequest.getFirstLine().toString(), normalHeaders, null);
    }

    @Test
    public void testNormalRequest() {
        new HttpRequest(session, null, normalRequest);
    }

}
