package ru.babobka.vsjws.util;

import org.junit.Test;
import ru.babobka.vsjws.model.Param;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by 123 on 12.06.2017.
 */
public class HttpUtilTest {

    @Test
    public void testReadBody() throws IOException {
        BufferedReader bufferedReader = mock(BufferedReader.class);
        when(bufferedReader.read()).thenReturn((int) 'X');
        String body = HttpUtil.readBody(10, bufferedReader);
        assertEquals(body.length(), 10);
        assertEquals(body, "XXXXXXXXXX");

    }

    @Test
    public void testGetUriParams() {
        String uri = "?abc=1&xyz=2";
        Set<Param> params = HttpUtil.getUriParams(uri);
        assertEquals(params.size(), 2);
        assertTrue(params.contains(new Param("abc", "1")));
        assertTrue(params.contains(new Param("xyz", "2")));
    }

    @Test
    public void testGetUriParamsInvalidUri() {
        String uri = "oh yeah";
        Set<Param> paramSet = HttpUtil.getUriParams(uri);
        assertTrue(paramSet.isEmpty());
    }

    @Test
    public void testGetHeaderValue() {
        String headerLine = "abc:123";
        assertEquals(HttpUtil.getHeaderValue(headerLine), "123");
    }

    @Test
    public void testGetHeaderValueSpaced() {
        String headerLine = "abc : 123";
        assertEquals(HttpUtil.getHeaderValue(headerLine), "123");
    }

    @Test
    public void testGetHeaderValueExtraDots() {
        String headerLine = "abc::123:123";
        assertEquals(HttpUtil.getHeaderValue(headerLine), ":123:123");
    }

    @Test
    public void testGetParams() {
        String paramText = "abc=1&xyz=2";
        Set<Param> params = HttpUtil.getParams(paramText);
        assertTrue(params.contains(new Param("abc", "1")));
        assertTrue(params.contains(new Param("xyz", "2")));
    }
}
