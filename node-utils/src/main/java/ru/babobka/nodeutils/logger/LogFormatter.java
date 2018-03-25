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

final class LogFormatter extends Formatter {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private final boolean debugMode;

    public LogFormatter(boolean debugMode) {
        this.debugMode = debugMode;
    }

    @Override
    public String format(LogRecord record) {
        StringBuilder logBuilder;
        if (debugMode) {
            logBuilder = debugFormat(record);
        } else {
            logBuilder = regularFormat(record);
        }
        if (record.getThrown() != null) {
            try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw)) {
                record.getThrown().printStackTrace(pw);
                pw.close();
                logBuilder.append(sw.toString());
            } catch (IOException e) {
                throw new IllegalStateException("cannot init logger " + e);
            }
        }
        return logBuilder.toString();
    }

    private StringBuilder debugFormat(LogRecord record) {
        StringBuilder sb = new StringBuilder();
        return sb.append(new Date(record.getMillis()))
                .append(" ").append(record.getLevel().getLocalizedName())
                .append(" thread ").append(Thread.currentThread().getId()).append(" : ")
                .append(formatMessage(record)).append(LINE_SEPARATOR);
    }

    private StringBuilder regularFormat(LogRecord record) {
        StringBuilder sb = new StringBuilder();
        return sb.append(new Date(record.getMillis()))
                .append(" ").append(record.getLevel().getLocalizedName()).append(" ")
                .append(formatMessage(record)).append(LINE_SEPARATOR);
    }
}