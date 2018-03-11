package ru.babobka.vsjws.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by 123 on 12.06.2017.
 */
public class HeaderTest {


    @Test
    public void testHeader() {
        Header header = new Header("key:value");
        assertEquals(header.getKey(), "key");
        assertEquals(header.getValue(), "value");
    }

    @Test
    public void testHeaderSpaced() {
        Header header = new Header("key : value");
        assertEquals(header.getKey(), "key");
        assertEquals(header.getValue(), "value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalLength() {
        new Header("abc");
    }
}
