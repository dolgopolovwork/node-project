package ru.babobka.nodeutils.container;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

public class ContainerTest {

	@BeforeClass
	public static void setUp() {
		Container.getInstance().put("abc");
		Container.getInstance().put(new Integer(123));
		Container.getInstance().put(new B());

	}

	@Test
	public void testGetByClass() {
		assertEquals("abc", Container.getInstance().get(String.class));
	}

	@Test
	public void testGetBySuperclass() {
		assertEquals(new Integer(123), Container.getInstance().get(Number.class));
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
