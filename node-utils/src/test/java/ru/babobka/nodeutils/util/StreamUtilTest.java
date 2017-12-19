package ru.babobka.nodeutils.util;

import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

public class StreamUtilTest {

    private final static String ACTUAL_TEXT = "First line\nSecond line";
    private final StreamUtil streamUtil = new StreamUtil();

    @Test
    public void readFileTest() throws IOException {
        String text = streamUtil.readFile("src/test/resources/test.txt");
        assertFalse(text.isEmpty());
        assertEquals(ACTUAL_TEXT, text);
    }

    @Test(expected = FileNotFoundException.class)
    public void readNonExistingFileTest() throws IOException {
        streamUtil.readFile("src/test/resources/test1.txt");
    }

    @Test
    public void getFileResourceTest() throws FileNotFoundException {
        assertNotNull(streamUtil.getLocalResource(this.getClass(), "test.txt"));
    }

    @Test(expected = FileNotFoundException.class)
    public void getNonExistingFileResourceTest() throws FileNotFoundException {
        assertNotNull(streamUtil.getLocalResource(this.getClass(), "test1.txt"));
    }

    @Test
    public void readByInputStreamTest() throws IOException {
        InputStream is = streamUtil.getLocalResource(this.getClass(), "test.txt");
        String text = streamUtil.readFile(is);
        assertFalse(text.isEmpty());
        assertEquals(ACTUAL_TEXT, text);
    }
}