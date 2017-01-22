package ru.babobka.nodeutils.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class TextUtilTest {

	@Test
	public void testNoNull() {
		assertNotNull(TextUtil.notNull(null));
	}

	@Test
	public void testToUrl() {
		String url = "abc$";
		assertEquals(TextUtil.toURL(url),"abc-");
		url = "Hello World";
		assertEquals(TextUtil.toURL(url),"hello-world");

	}

	@Test
	public void testTryParseLong() {
		long l = TextUtil.tryParseLong("123", -1);
		assertEquals(l, 123);
		l = TextUtil.tryParseLong("hello world", -1);
		assertEquals(l, -1);
	}

	@Test
	public void testTryParseInt() {
		int i = TextUtil.tryParseInt("123", -1);
		assertEquals(i, 123);
		i = TextUtil.tryParseInt("hello world", -1);
		assertEquals(i, -1);
	}

}
