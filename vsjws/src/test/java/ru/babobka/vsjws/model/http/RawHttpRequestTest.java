package ru.babobka.vsjws.model.http;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RawHttpRequestTest {

    @Test
    public void testFirstLine() {
        RawHttpRequest.FirstLine firstLine = new RawHttpRequest.FirstLine("GET / HTTP/1.1");
        assertEquals(firstLine.getMethod(), "GET");
        assertEquals(firstLine.getUri(), "/");
        assertEquals(firstLine.getProtocol(), "HTTP/1.1");
    }

    @Test
    public void testFirsLineLongUri() {
        RawHttpRequest.FirstLine firstLine = new RawHttpRequest.FirstLine("GET /Hey/I am a really long URI HTTP/1.1");
        assertEquals(firstLine.getMethod(), "GET");
        assertEquals(firstLine.getUri(), "/Hey/I am a really long URI");
        assertEquals(firstLine.getProtocol(), "HTTP/1.1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidFirstLine() {
        new RawHttpRequest.FirstLine("abc");
    }

}
