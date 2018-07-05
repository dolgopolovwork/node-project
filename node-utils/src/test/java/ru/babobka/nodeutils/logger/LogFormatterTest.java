package ru.babobka.nodeutils.logger;

import org.junit.Test;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created by 123 on 01.07.2018.
 */
public class LogFormatterTest {

    private LogFormatter logFormatter;

    @Test(expected = IllegalArgumentException.class)
    public void testFormatNullRecord() {
        logFormatter = new LogFormatter(false);
        logFormatter.format(null);
    }

    @Test
    public void testFormat() {
        String newFieldSplitter = ";";
        LogRecord logRecord = mock(LogRecord.class);
        Level level = mock(Level.class);
        when(logRecord.getLevel()).thenReturn(level);
        when(level.getLocalizedName()).thenReturn("test level");
        logFormatter = spy(new LogFormatter(false));
        String logText = "test";
        doReturn(logText).when(logFormatter).formatMessage(logRecord);
        doReturn(newFieldSplitter).when(logFormatter).getNewFieldSplitter();
        String formatterLogText = logFormatter.format(logRecord);
        String[] utilLogPart = formatterLogText.split(newFieldSplitter);
        assertEquals(utilLogPart.length, 3);
        assertEquals(utilLogPart[1], logRecord.getLevel().getLocalizedName());
        assertEquals(utilLogPart[2].trim(), logText);
    }

    @Test
    public void testFormatDebug() {
        String newFieldSplitter = ";";
        LogRecord logRecord = mock(LogRecord.class);
        Level level = mock(Level.class);
        when(logRecord.getLevel()).thenReturn(level);
        when(level.getLocalizedName()).thenReturn("test level");
        logFormatter = spy(new LogFormatter(true));
        String logText = "test";
        doReturn(logText).when(logFormatter).formatMessage(logRecord);
        doReturn(newFieldSplitter).when(logFormatter).getNewFieldSplitter();
        String formatterLogText = logFormatter.format(logRecord);
        String[] utilLogPart = formatterLogText.split(newFieldSplitter);
        assertEquals(utilLogPart.length, 4);
        assertEquals(utilLogPart[1], logRecord.getLevel().getLocalizedName());
        assertEquals(utilLogPart[2], "thread " + Thread.currentThread().getId());
        assertEquals(utilLogPart[3].trim(), logText);
    }
}