package ru.babobka.nodeutils.util;

import org.junit.Test;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Created by 123 on 01.09.2017.
 */
public class HashUtilTest {

    @Test(expected = IllegalArgumentException.class)
    public void testSha2NullMessage() {
        HashUtil.sha2((String) null);
    }

    @Test
    public void testSha2() {
        byte[] hash = HashUtil.sha2("abc");
        assertArrayEquals(hash, HashUtil.hexStringToByteArray("BA7816BF8F01CFEA414140DE5DAE2223B00361A396177A9CB410FF61F20015AD"));
        assertEquals(hash.length, 32);
    }

    @Test
    public void testSafeHashCodeNull() {
        assertEquals(0, HashUtil.safeHashCode(null));
    }

    @Test
    public void testSafeHashCode() {
        String s = "abc";
        assertEquals(s.hashCode(), HashUtil.safeHashCode(s));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHexSha2NullMessage() {
        HashUtil.hexSha2(null);
    }

    @Test
    public void testHexSha2() {
        for (int i = 0; i < 1000; i++) {
            String message = String.valueOf(i);
            assertArrayEquals(HashUtil.hexStringToByteArray(HashUtil.hexSha2(message)), HashUtil.sha2(message));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSha2IteratorNull() {
        HashUtil.sha2((Iterator) null);
    }

    @Test
    public void testSha2IteratorSameObject() {
        for (int i = 0; i < 100; i++) {
            Map<String, Serializable> map = randomMap();
            assertArrayEquals(HashUtil.sha2(map.entrySet().iterator()), HashUtil.sha2(map.entrySet().iterator()));
        }
    }

    private Map<String, Serializable> randomMap() {
        Map<String, Serializable> map = new TreeMap<>();
        Random random = new Random();
        int elements = random.nextInt(25);
        for (int i = 0; i < elements; i++) {
            map.put(String.valueOf(i), String.valueOf(random.nextInt()));
        }
        return map;
    }
}
