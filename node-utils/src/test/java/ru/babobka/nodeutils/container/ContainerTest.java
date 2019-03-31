package ru.babobka.nodeutils.container;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class ContainerTest {

    @Before
    public void setUp() {
        Container.getInstance().put("abc");
        Container.getInstance().put(Integer.valueOf(123));
        Container.getInstance().put(new B());
        Container.getInstance().put(StandardCharsets.UTF_8);
        Container.getInstance().put(TestKey.XYZ, Integer.valueOf(123));
    }

    @After
    public void tearDown() {
        Container.getInstance().clear();
    }

    @Test
    public void testGetByClass() {
        assertEquals("abc", Container.getInstance().get(String.class));
    }

    @Test
    public void testGetCharset() {
        assertNotNull(Container.getInstance().get(Charset.class));
    }

    @Test(expected = NullPointerException.class)
    public void testPutNull() {
        Container.getInstance().put((AbstractApplicationContainer) null);
    }

    @Test
    public void testGetByKey() {
        assertEquals(Container.getInstance().get(TestKey.XYZ), Integer.valueOf(123));
    }

    @Test(expected = ContainerException.class)
    public void testGetByKeyNotExisting() {
        Container.getInstance().get(TestKey.ABC);
    }

    @Test(expected = NullPointerException.class)
    public void testPutNullKey() {
        Container.getInstance().put(null, new Object());
    }

    @Test(expected = NullPointerException.class)
    public void testPutNullObject() {
        Container.getInstance().put(TestKey.ABC, null);
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

    @Test(expected = NullPointerException.class)
    public void testPutIfNotExistsNull() {
        Container.getInstance().putIfAbsent(null);
    }

    @Test
    public void testPutIfNotExists() {
        Double number = 3.14;
        Container.getInstance().putIfAbsent(number);
        assertEquals(Container.getInstance().get(Double.class), number);
    }

    @Test
    public void testPutIfNotExistsTwice() {
        Double number = 3.14;
        Double secondNumber = 2.7;
        Container.getInstance().putIfAbsent(number);
        Container.getInstance().putIfAbsent(secondNumber);
        assertEquals(Container.getInstance().get(Double.class), number);
    }

    @Test
    public void testGetDefault() {
        int defaultValue = 1;
        assertEquals((int) Container.getInstance().get(TestKey.ABC, defaultValue), defaultValue);
    }

    @Test
    public void testGetDefaultContains() {
        int defaultValue = 1;
        int realValue = 2;
        Container.getInstance().put(TestKey.ABC, realValue);
        assertEquals((int) Container.getInstance().get(TestKey.ABC, defaultValue), realValue);
    }

    interface A {

    }

    static class B implements A {

    }

    enum TestKey implements Key {
        ABC, XYZ
    }

}
