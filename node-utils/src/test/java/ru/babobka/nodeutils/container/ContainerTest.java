package ru.babobka.nodeutils.container;

import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class ContainerTest {

    @BeforeClass
    public static void setUp() {
        Container.getInstance().put("abc");
        Container.getInstance().put(Integer.valueOf(123));
        Container.getInstance().put(new B());
        Container.getInstance().put(StandardCharsets.UTF_8);
        Container.getInstance().put("key", Integer.valueOf(123));
    }

    @Test
    public void testGetByClass() {
        assertEquals("abc", Container.getInstance().get(String.class));
    }

    @Test
    public void testGetCharset() {
        assertNotNull(Container.getInstance().get(Charset.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNull() {
        Container.getInstance().put(null);
    }

    @Test
    public void testGetByKey() {
        assertEquals(Container.getInstance().get("key"), Integer.valueOf(123));
    }

    @Test(expected = ContainerException.class)
    public void testGetByKeyNotExisting() {
        Container.getInstance().get("abc");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullKey() {
        Container.getInstance().put(null, new Object());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullObject() {
        Container.getInstance().put("abc", null);
    }


    @Test
    public void testGetBySuperclass() {
        assertEquals(Integer.valueOf(123), Container.getInstance().get(Number.class));
    }

    @Test
    public void testGetByInterface() {
        assertNotEquals(Container.getInstance().get(A.class), null);
    }

    @Test(expected = ContainerException.class)
    public void testNonExisting() {
        Container.getInstance().get(Thread.class);
    }

    interface A {

    }

    static class B implements A {

    }

}
