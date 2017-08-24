package ru.babobka.nodeutils.util;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

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
}
