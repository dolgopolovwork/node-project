package ru.babobka.nodeutils.util;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

public class StreamUtilTest {

	final static String ACTUAL_TEXT = "First line\nSecond line\nПроверка кодировки";

	@Test
	public void readFileTest() throws IOException {
		String text = StreamUtil.readFile("src/test/resources/test.txt");
		assertFalse(text.isEmpty());
		assertEquals(ACTUAL_TEXT, text);
	}

	@Test(expected = FileNotFoundException.class)
	public void readNonExistingFileTest() throws IOException {
		StreamUtil.readFile("src/test/resources/test1.txt");
	}

	@Test
	public void getFileResourceTest() throws FileNotFoundException {
		assertNotNull(StreamUtil.getLocalResource(this.getClass(), "test.txt"));
	}

	@Test(expected = FileNotFoundException.class)
	public void getNonExistingFileResourceTest() throws FileNotFoundException {
		assertNotNull(StreamUtil.getLocalResource(this.getClass(), "test1.txt"));
	}

	@Test
	public void readByInputStreamTest() throws IOException {
		InputStream is = StreamUtil.getLocalResource(this.getClass(), "test.txt");
		String text = StreamUtil.readFile(is);
		assertFalse(text.isEmpty());
		assertEquals(ACTUAL_TEXT, text);

	}
}
