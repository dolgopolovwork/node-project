package ru.babobka.nodeutils.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

}
