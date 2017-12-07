package ru.babobka.nodeutils.util;

import org.junit.Test;

import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TextUtilTest {

    @Test
    public void testNotNull() {
        assertNotNull(TextUtil.notNull(null));
    }

    @Test
    public void testNotNullOriginalMessage() {
        String message = "abc";
        assertEquals(message, TextUtil.notNull(message));
    }

    @Test
    public void testToUrl() {
        String url = "abc$";
        assertEquals(TextUtil.toURL(url), "abc-");
        url = "Hello World";
        assertEquals(TextUtil.toURL(url), "hello-world");

    }

    @Test
    public void testTryParseLong() {
        long l = TextUtil.tryParseLong("123", -1);
        assertEquals(l, 123);
        l = TextUtil.tryParseLong("hello world", -1);
        assertEquals(l, -1);
    }

    @Test
    public void testTryParseInt() {
        int i = TextUtil.tryParseInt("123", -1);
        assertEquals(i, 123);
        i = TextUtil.tryParseInt("hello world", -1);
        assertEquals(i, -1);
    }

    @Test
    public void testValidEmail() {
        String email = "dolgopolov.work@gmail.com";
        assertTrue(TextUtil.isValidEmail(email));
    }

    @Test
    public void testInvalidEmail() {
        String email = "dolgopolov.work@com";
        assertFalse(TextUtil.isValidEmail(email));
    }

    @Test
    public void testNullEmail() {
        assertFalse(TextUtil.isValidEmail(null));
    }

    @Test
    public void testEmptyNull() {
        assertTrue(TextUtil.isEmpty(null));
    }

    @Test
    public void testEmptyString() {
        assertTrue(TextUtil.isEmpty(""));
    }

    @Test
    public void testNotEmpty() {
        assertFalse(TextUtil.isEmpty("abc"));
    }

    @Test
    public void testIsValidUUID() {
        assertTrue(TextUtil.isValidUUID(UUID.randomUUID().toString()));
    }

    @Test
    public void testIsValidUUIDEmptyString() {
        assertFalse(TextUtil.isValidUUID(""));
    }

    @Test
    public void testIsValidUUIDNullString() {
        assertFalse(TextUtil.isValidUUID(null));
    }

    @Test
    public void testIsValidUUIDTrash() {
        assertFalse(TextUtil.isValidUUID("abc"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReadJsonFileNullPath() throws IOException {
        TextUtil.readJsonFile(mock(StreamUtil.class), null, Serializable.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReadJsonFileEmptyPath() throws IOException {
        TextUtil.readJsonFile(mock(StreamUtil.class), "", Serializable.class);
    }

    @Test(expected = IOException.class)
    public void testCreateInvalidJson() throws IOException {
        StreamUtil streamUtil = mock(StreamUtil.class);
        String path = "C://path";
        when(streamUtil.readFile(path)).thenReturn("not json at all");
        TextUtil.readJsonFile(streamUtil, path, Serializable.class);
    }

    @Test(expected = IOException.class)
    public void testReadJsonFileIOExceptionWhileRead() throws IOException {
        StreamUtil streamUtil = mock(StreamUtil.class);
        String path = "C://path";
        when(streamUtil.readFile(path)).thenThrow(new IOException());
        TextUtil.readJsonFile(streamUtil, path, Serializable.class);
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
        TestPojo pojo = TextUtil.readJsonFile(streamUtil, path, TestPojo.class);
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
