package ru.babobka.vsjws.model.http;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RawHttpRequestTest {

    @Test
    public void testFirstLine() {
        HttpFirstLine firstLine = new HttpFirstLine("GET / HTTP/1.1");
        assertEquals(firstLine.getMethod(), "GET");
        assertEquals(firstLine.getUri(), "/");
        assertEquals(firstLine.getProtocol(), "HTTP/1.1");
    }

    @Test
    public void testFirsLineLongUri() {
        HttpFirstLine firstLine = new HttpFirstLine("GET /Hey/I am a really long URI HTTP/1.1");
        assertEquals(firstLine.getMethod(), "GET");
        assertEquals(firstLine.getUri(), "/Hey/I am a really long URI");
        assertEquals(firstLine.getProtocol(), "HTTP/1.1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidFirstLine() {
        new HttpFirstLine("abc");
    }

}
