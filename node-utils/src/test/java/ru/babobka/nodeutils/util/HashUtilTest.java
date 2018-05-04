package ru.babobka.nodeutils.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by 123 on 01.09.2017.
 */
public class HashUtilTest {

    @Test(expected = IllegalArgumentException.class)
    public void testSha2NullMessage() {
        HashUtil.sha2((String)null);
    }

    @Test
    public void testSha2() {
        byte[] hash = HashUtil.sha2("abc");
        assertEquals(hash.length, 32);
    }
}
