package ru.babobka.nodeutils.util;

import org.junit.Test;

import java.io.IOException;
import java.io.Serializable;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by 123 on 05.03.2018.
 */
public class JSONUtilTests {

    @Test
    public void testIsValidJsonNull() {
        assertFalse(JSONUtil.isJSONValid(null));
    }

    @Test
    public void testIsValidJsonEmpty() {
        assertFalse(JSONUtil.isJSONValid(""));
    }

    @Test
    public void testIsValidJsonNoBraces() {
        assertFalse(JSONUtil.isJSONValid("abc"));
    }

    @Test
    public void testIsValidJsonBadBraces() {
        assertFalse(JSONUtil.isJSONValid("{}abc"));
        assertFalse(JSONUtil.isJSONValid("abc{}"));
        assertFalse(JSONUtil.isJSONValid("}abc{"));
    }

    @Test
    public void testIsValidJson() {
        assertTrue(JSONUtil.isJSONValid("{\"abc\":123}"));
    }

    @Test
    public void testIsValidJsonArray() {
        assertTrue(JSONUtil.isJSONValid("[ \"Ford\", \"BMW\", \"Fiat\" ]"));
    }

    @Test
    public void testIsValidJsonInvalid() {
        assertFalse(JSONUtil.isJSONValid("{kek}"));
    }

    @Test(expected = NullPointerException.class)
    public void testReadJsonFileNullPath() throws IOException {
        JSONUtil.readJsonFile(mock(StreamUtil.class), null, Serializable.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReadJsonFileEmptyPath() throws IOException {
        JSONUtil.readJsonFile(mock(StreamUtil.class), "", Serializable.class);
    }

    @Test(expected = IOException.class)
    public void testCreateInvalidJson() throws IOException {
        StreamUtil streamUtil = mock(StreamUtil.class);
        String path = "C://path";
        when(streamUtil.readFile(path)).thenReturn("not json at all");
        JSONUtil.readJsonFile(streamUtil, path, Serializable.class);
    }

    @Test(expected = IOException.class)
    public void testReadJsonFileIOExceptionWhileRead() throws IOException {
        StreamUtil streamUtil = mock(StreamUtil.class);
        String path = "C://path";
        when(streamUtil.readFile(path)).thenThrow(new IOException());
        JSONUtil.readJsonFile(streamUtil, path, Serializable.class);
    }

    @Test
    public void testCreate() throws IOException {
        StreamUtil streamUtil = mock(StreamUtil.class);
        String path = "C://path";
        String json = "{\n" +
                "   \"bool\":true,\n" +
                "   \"number\":2000,\n" +
                "   \"text\":\"abc\"\n" + "}";
        when(streamUtil.readFile(path)).thenReturn(json);
        JSONUtilTests.TestPojo pojo = JSONUtil.readJsonFile(streamUtil, path, JSONUtilTests.TestPojo.class);
        assertEquals(pojo.isBool(), true);
        assertEquals(pojo.getNumber(), 2000);
        assertEquals(pojo.getText(), "abc");
    }

    private static class TestPojo implements Serializable {
        private static final long serialVersionUID = 3748573513916924453L;
        private String text;
        private int number;
        private boolean bool;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public boolean isBool() {
            return bool;
        }

        public void setBool(boolean bool) {
            this.bool = bool;
        }
    }
}
