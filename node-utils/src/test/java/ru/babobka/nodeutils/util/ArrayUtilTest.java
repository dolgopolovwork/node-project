package ru.babobka.nodeutils.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by 123 on 21.10.2017.
 */
public class ArrayUtilTest {

    @Test
    public void testIsNullOneNull() {
        assertTrue(ArrayUtil.isNull(null));
    }

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

    @Test
    public void testIsEmptyNullBytes() {
        assertTrue(ArrayUtil.isEmpty((byte[]) null));
    }

    @Test
    public void testIsEmptyNullInt() {
        assertTrue(ArrayUtil.isEmpty((int[]) null));
    }

    @Test
    public void testIsEmpty() {
        assertTrue(ArrayUtil.isEmpty(new byte[]{}));
    }

    @Test
    public void testIsEmptyNotEmpty() {
        assertFalse(ArrayUtil.isEmpty(new byte[]{1, 2, 3}));
    }

    @Test
    public void testIsUniqueNull() {
        assertTrue(ArrayUtil.isUnique(null));
    }

    @Test
    public void testIsUniqueEmpty() {
        assertTrue(ArrayUtil.isUnique());
    }

    @Test
    public void testIsUniqueOneElement() {
        assertTrue(ArrayUtil.isUnique(0));
    }

    @Test
    public void testIsUniqueNotUnique() {
        assertFalse(ArrayUtil.isUnique(1, 2, 3, 4, 5, 6, 7, 8, 6));
    }

    @Test
    public void testIsUnique() {
        assertTrue(ArrayUtil.isUnique(1, 2, 3, 4, 5, 6, 7, 8));
    }

    @Test
    public void testIsUniqueTwoElements() {
        assertTrue(ArrayUtil.isUnique(1, 2));
    }

    @Test
    public void testIsNotUniqueTwoElements() {
        assertFalse(ArrayUtil.isUnique(2, 2));
    }
}