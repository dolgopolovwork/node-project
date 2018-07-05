package ru.babobka.nodeutils.logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Created by dolgopolov.a on 12.01.16.
 */

class LogFormatter extends Formatter {

    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String NEW_FIELD = "\t|\t";
    private final boolean debugMode;

    public LogFormatter(boolean debugMode) {
        this.debugMode = debugMode;
    }

    @Override
    public String format(LogRecord record) {
        if (record == null) {
            throw new IllegalArgumentException("record is null");
        }
        StringBuilder logBuilder;
        if (debugMode) {
            logBuilder = debugFormat(record);
        } else {
            logBuilder = regularFormat(record);
        }
        if (record.getThrown() == null) {
            return logBuilder.toString();
        }
        try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw)) {
            record.getThrown().printStackTrace(pw);
            logBuilder.append(sw.toString());
        } catch (IOException e) {
            throw new IllegalStateException("cannot format logger ", e);
        }
        return logBuilder.toString();
    }


    private StringBuilder debugFormat(LogRecord record) {
        StringBuilder sb = new StringBuilder();
        return sb.append(new Date(record.getMillis()))
                .append(getNewFieldSplitter())
                .append(record.getLevel().getLocalizedName())
                .append(getNewFieldSplitter())
                .append("thread ").append(Thread.currentThread().getId())
                .append(getNewFieldSplitter())
                .append(formatMessage(record))
                .append(NEW_LINE);
    }

    private StringBuilder regularFormat(LogRecord record) {
        StringBuilder sb = new StringBuilder();
        return sb.append(new Date(record.getMillis()))
                .append(getNewFieldSplitter())
                .append(record.getLevel().getLocalizedName())
                .append(getNewFieldSplitter())
                .append(formatMessage(record))
                .append(NEW_LINE);
    }

    String getNewFieldSplitter() {
        return NEW_FIELD;
    }
}