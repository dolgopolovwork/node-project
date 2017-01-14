package ru.babobka.nodeutils.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public interface TextUtil {

	Charset CHARSET = StandardCharsets.UTF_8;

	static String notNull(String s) {
		if (s == null) {
			return "";
		}
		return s;
	}

	static long tryParseLong(String s, long defaultValue) {
		try {
			return Long.parseLong(s);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	static int tryParseInt(String s, int defaultValue) {
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public static String getStringFromException(Exception ex) {
		StringWriter errors = new StringWriter();
		ex.printStackTrace(new PrintWriter(errors));
		return errors.toString();
	}

	public static String beautifyServerName(String serverName, int port) {
		return "'" + serverName + "':" + port;
	}

}
