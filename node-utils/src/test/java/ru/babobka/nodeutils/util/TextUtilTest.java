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

    @Test(expected = IllegalArgumentException.class)
    public void testGetLongestRepeatsNoText() {
        TextUtil.getLongestRepeats("", '1');
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetLongestRepeatsNullText() {
        TextUtil.getLongestRepeats(null, '1');
    }

    @Test
    public void testGetLongestRepeatsNoSymbol() {
        assertEquals(getMaxElement(TextUtil.getLongestRepeats("987654", '1')), 0);
    }

    @Test
    public void testGetLongestRepeatsFullMatch() {
        assertEquals(getMaxElement(TextUtil.getLongestRepeats("111", '1')), 3);
    }

    @Test
    public void testGetLongestRepeats() {
        assertArrayEquals(TextUtil.getLongestRepeats("1011101101", '1'), new int[]{1, 0, 1, 2, 3, 0, 1, 2, 0, 1});
    }

    @Test
    public void testGetFirstNonNullNull() {
        assertNull(TextUtil.getFirstNonNull(null));
    }

    @Test
    public void testGetFirstNonNullEmpty() {
        assertNull(TextUtil.getFirstNonNull());
    }

    @Test
    public void testGetFirstNonNullAllNulls() {
        assertNull(TextUtil.getFirstNonNull(null, null, null));
    }

    @Test
    public void testGetFirstNonNullFirst() {
        String[] strings = {"abc", "xyz", "qwe"};
        for (int i = 0; i < strings.length; i++) {
            assertEquals(strings[i], TextUtil.getFirstNonNull(strings));
            strings[i] = null;
        }
    }

    private static int getMaxElement(int[] array) {
        int max = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }
        return max;
    }
}
