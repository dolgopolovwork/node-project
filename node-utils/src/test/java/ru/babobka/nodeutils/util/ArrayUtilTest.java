package ru.babobka.nodeutils.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by 123 on 21.10.2017.
 */
public class ArrayUtilTest {

    @Test
    public void testIsNull() {
        assertTrue(ArrayUtil.isNull(null, null));
    }

    @Test
    public void testIsNullLastElement() {
        assertTrue(ArrayUtil.isNull("test", null));
    }

    @Test
    public void testIsNotNull() {
        assertFalse(ArrayUtil.isNull("test", "qwerty"));
    }

}