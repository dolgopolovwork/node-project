package ru.babobka.nodeutils.util;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.junit.Test;

import ru.babobka.subtask.model.SubTask;

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

	@Test
	public void getAllTasksTest() throws IOException {
		List<SubTask> subTasks = StreamUtil.getSubtasks("src/test/resources/tasks/prime-counter-task-1.0-SNAPSHOT.jar");
		assertEquals(subTasks.size(), 1);
		subTasks = StreamUtil
				.getSubtasks("src/test/resources/tasks/factor-task-1.0-SNAPSHOT-jar-with-dependencies.jar");
		assertEquals(subTasks.size(), 1);
	}
}
