package ru.babobka.nodeutils.container;

import static org.junit.Assert.*;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.junit.BeforeClass;
import org.junit.Test;

public class ContainerTest {

	@BeforeClass
	public static void setUp() {
		Container.getInstance().put("abc");
		Container.getInstance().put(Integer.valueOf(123));
		Container.getInstance().put(new B());
		Container.getInstance().put(StandardCharsets.UTF_8);

	}

	@Test
	public void testGetByClass() {
		assertEquals("abc", Container.getInstance().get(String.class));
	}

	@Test
	public void testGetCharset() {

		assertNotNull(Container.getInstance().get(Charset.class));
	}

	@Test
	public void testGetBySuperclass() {
		assertEquals(Integer.valueOf(123), Container.getInstance().get(Number.class));
	}

	@Test
	public void testGetByInterface() {
		assertNotEquals(Container.getInstance().get(A.class), null);
	}

	@Test
	public void testNonExisting() {
		assertNull(Container.getInstance().get(Thread.class));
	}

	static interface A {

	}

	static class B implements A {

	}
	


}
