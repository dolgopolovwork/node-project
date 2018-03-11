package ru.babobka.vsjws.mapper;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import ru.babobka.vsjws.enumerations.HttpMethod;
import ru.babobka.vsjws.model.http.HttpRequest;
import ru.babobka.vsjws.model.http.HttpSession;
import ru.babobka.vsjws.model.json.JSONRequest;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by 123 on 24.04.2017.
 */
public class JSONRequestMapperTest {

    private static final String JSON = "{\"id\": 1, \"name\": \"A green door\",  \"price\": 12.50, \"tags\": [\"home\", \"green\"]}";
    private static final String URI = "test";
    private JSONRequestMapper mapper = new JSONRequestMapper();
    private HttpSession session = mock(HttpSession.class);

    @Test
    public void testMap() throws UnknownHostException {
        InetAddress inetAddress = InetAddress.getLocalHost();
        HttpRequest request = mock(HttpRequest.class);
        when(request.getBody()).thenReturn(JSON);
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        when(request.getUri()).thenReturn(URI);
        when(request.getAddress()).thenReturn(inetAddress);
        JSONRequest jsonRequest = mapper.map(session, request);
        assertEquals(new JSONObject(JSON).toString(), jsonRequest.getBody().toString());
        assertEquals(jsonRequest.getAddress(), inetAddress);
        assertEquals(jsonRequest.getUri(), URI);

    }

    @Test
    public void testEmptyBody() throws UnknownHostException {
        InetAddress inetAddress = InetAddress.getLocalHost();
        HttpRequest request = mock(HttpRequest.class);
        when(request.getBody()).thenReturn("");
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        when(request.getUri()).thenReturn(URI);
        when(request.getAddress()).thenReturn(inetAddress);

        JSONRequest jsonRequest = mapper.map(session, request);
        assertEquals("{}", jsonRequest.getBody().toString());
        assertEquals(jsonRequest.getAddress(), inetAddress);
        assertEquals(jsonRequest.getUri(), URI);
    }


    @Test(expected = JSONException.class)
    public void testInvalidJSON() {
        HttpRequest request = mock(HttpRequest.class);
        when(request.getBody()).thenReturn("123");
        mapper.map(session, request);
    }
}
