package ru.babobka.nodeutils.logger;

import org.junit.Test;
import ru.babobka.nodeutils.util.TextUtil;

import java.util.logging.Logger;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by 123 on 01.09.2017.
 */
public class NodeLoggerTest {

    private final Logger logger = mock(Logger.class);

    private final NodeLogger nodeLogger = new SimpleLogger(logger);

    @Test
    public void testInfo() {
        String message = "abc";
        nodeLogger.info(message);
        verify(logger).info(message);
    }

    @Test
    public void testInfoObject() {
        Object object = new Object();
        nodeLogger.info(object);
        verify(logger).info(object.toString());
    }

    @Test
    public void testWarning() {
        String message = "abc";
        nodeLogger.warning(message);
        verify(logger).warning(message);
    }

    @Test
    public void testWarningException() {
        Exception exception = new Exception("test exception");
        nodeLogger.warning(exception);
        verify(logger).warning(TextUtil.getStringFromException(exception));
    }

    @Test
    public void testError() {
        String message = "abc";
        nodeLogger.error(message);
        verify(logger).severe(message);
    }

    @Test
    public void testErrorException() {
        Exception exception = new Exception("test exception");
        nodeLogger.error(exception);
        verify(logger).severe(TextUtil.getStringFromException(exception));
    }
}
