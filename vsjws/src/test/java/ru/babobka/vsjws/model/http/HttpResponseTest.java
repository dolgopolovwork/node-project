package ru.babobka.vsjws.model.http;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import ru.babobka.vsjws.enumerations.ContentType;
import ru.babobka.vsjws.enumerations.ResponseCode;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class HttpResponseTest {

    private static final String JSON = "{\"id\": 1, \"name\": \"A green door\",  \"price\": 12.50, \"tags\": [\"home\", \"green\"]}";

    @Test
    public void testOkResponse() {
        HttpResponse response = ResponseFactory.ok();
        assertEquals(response.getResponseCode(), ResponseCode.OK);
        assertNotEquals(response.getContent().length, 0);
    }

    @Test
    public void testExceptionResponse() {
        HttpResponse response = ResponseFactory.exception(new NullPointerException());
        assertNotEquals(response.getResponseCode(), ResponseCode.OK);
        assertNotEquals(response.getContent().length, 0);
    }

    @Test
    public void testEmptyResponse() {
        HttpResponse response = ResponseFactory.noContent();
        assertEquals(response.getResponseCode(), ResponseCode.NO_CONTENT);
        assertEquals(response.getContent().length, 0);
    }

    @Test
    public void testFileResponse() throws IOException {
        File currentJavaFile = new File("pom.xml");
        HttpResponse response = ResponseFactory.file(currentJavaFile);
        assertEquals(response.getResponseCode(), ResponseCode.OK);
        assertEquals(response.getContent().length, 0);
        assertNotNull(response.getFile());
    }

    @Test(expected = IllegalStateException.class)
    public void testResourceResponse() {
        HttpResponse response = ResponseFactory.resource("test.txt");
        assertEquals(response.getResponseCode(), ResponseCode.OK);
        ResponseFactory.resource("test1.txt");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidJsonResponse() {
        ResponseFactory.json("fail");
    }

    @Test
    public void testSimpleJsonResponse() {
        HttpResponse response = ResponseFactory.json(new JSONObject());
        assertEquals(response.getResponseCode(), ResponseCode.OK);
        assertTrue(response.getContent().length > 0);
        assertNull(response.getFile());

    }

    @Test
    public void testJsonMapResponse() {
        String key = "abc";
        Map<String, String> testJsonMap = new HashMap<>();
        testJsonMap.put(key, "xyz");
        HttpResponse response = ResponseFactory.json(testJsonMap);
        JSONObject responseJSON = new JSONObject(new String(response.getContent()));
        JSONObject expectedJSON = new JSONObject(testJsonMap);
        assertEquals(responseJSON.toString(), expectedJSON.toString());
        assertFalse(responseJSON.isNull(key));
    }

    @Test
    public void testJsonObjectResponse() {
        Object objectToJson = new Object();
        HttpResponse response = ResponseFactory.json(objectToJson);
        JSONObject responseJSON = new JSONObject(new String(response.getContent()));
        JSONObject expectedJSON = new JSONObject(objectToJson);
        assertEquals(responseJSON.toString(), expectedJSON.toString());
        String dummyJson = "{\"test\":1}";
        response = ResponseFactory.json(dummyJson);
        assertEquals(new String(response.getContent()), dummyJson);
    }


    @Test
    public void testJsonStringResponse() {
        HttpResponse response = ResponseFactory.json(JSON);
        assertArrayEquals(JSON.getBytes(), response.getContent());
    }

    @Test
    public void testJsonResponseType() {
        HttpResponse response = ResponseFactory.json("{\"test\":1}");
        assertEquals(response.getContentType(), ContentType.JSON.toString());
    }

    @Test(expected = NullPointerException.class)
    public void testJsonNullResponse() {
        ResponseFactory.json(null);
    }

    @Test
    public void testJsonListResponse() {
        List<String> list = new LinkedList<>();
        list.add("abc");
        list.add("xyz");
        list.add("qwe");
        HttpResponse response = ResponseFactory.json(list);
        JSONArray responseJSON = new JSONArray(new String(response.getContent()));
        assertEquals(responseJSON.length(), list.size());
    }

    @Test
    public void testJsonArrayResponse() {
        int[] array = {1, 2, 3};
        HttpResponse response = ResponseFactory.json(array);
        assertTrue(response.getContent().length > 0);
        JSONArray responseJSON = new JSONArray(new String(response.getContent()));
        JSONArray expectedJSON = new JSONArray(array);
        assertEquals(responseJSON.toString(), expectedJSON.toString());
        assertEquals(responseJSON.length(), array.length);
    }

}
