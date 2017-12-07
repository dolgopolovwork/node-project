package ru.babobka.nodeutils.util;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public interface TextUtil {

    Charset CHARSET = StandardCharsets.UTF_8;

    String UUID_PATTERN = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";

    String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    Gson GSON = new Gson();

    static boolean isValidEmail(String email) {
        return email != null && email.matches(EMAIL_PATTERN);
    }

    static String notNull(String s) {
        if (s == null) {
            return "";
        }
        return s;
    }

    static boolean isValidUUID(String uuid) {
        return !isEmpty(uuid) && uuid.matches(UUID_PATTERN);
    }

    static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
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

    static String toURL(String text) {
        if (text != null)
            return text.toLowerCase(Locale.getDefault()).replaceAll("[^A-Za-z0-9]", "-");
        return "";
    }

    static String getStringFromException(Exception ex) {
        StringWriter errors = new StringWriter();
        ex.printStackTrace(new PrintWriter(errors));
        return errors.toString();
    }

    static String beautifyServerName(String serverName, int port) {
        return "'" + serverName + "':" + port;
    }

    static <T extends Serializable> T readJsonFile(StreamUtil streamUtil, String pathToJson, Class<T> clazz) throws IOException {
        if (streamUtil == null) {
            throw new IllegalArgumentException("streamUtil is null");
        } else if (TextUtil.isEmpty(pathToJson)) {
            throw new IllegalArgumentException("pathToJson is null");
        } else if (clazz == null) {
            throw new IllegalArgumentException("class was not specified");
        }
        String fileContent = streamUtil.readFile(pathToJson);
        try {
            return GSON.fromJson(fileContent, clazz);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

}
