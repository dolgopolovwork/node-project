package ru.babobka.nodeutils.util;

import lombok.NonNull;
import ru.babobka.nodeutils.enums.Env;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Locale;

public class TextUtil {

    public static final String WELCOME_TEXT = "Welcome to node-project (´｡• ᵕ •｡`) ♡";
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final String BAD_PASSWORD_PATTERN = "^[0-9a-z]+$";
    public static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final String UUID_PATTERN = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String DIGITS_PATTERN = "[0-9]+";

    private TextUtil() {
    }

    public static String toBase64(@NonNull byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static byte[] fromBase64(@NonNull String base64) {
        return Base64.getDecoder().decode(base64.getBytes(TextUtil.CHARSET));
    }

    public static boolean isValidEmail(String email) {
        return email != null && email.matches(EMAIL_PATTERN);
    }

    static String notNull(String s) {
        if (s == null) {
            return "";
        }
        return s;
    }

    public static String readStream(InputStream inputStream) throws IOException {
        StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(
                new InputStreamReader(inputStream, Charset.forName(StandardCharsets.UTF_8.name())))) {
            int c = 0;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
        }
        return textBuilder.toString();
    }

    public static boolean isNumber(String text) {
        return !isEmpty(text) && text.matches(DIGITS_PATTERN);
    }

    static boolean isValidUUID(String uuid) {
        return !isEmpty(uuid) && uuid.matches(UUID_PATTERN);
    }

    public static boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public static boolean isBadPassword(String password) {
        return (TextUtil.isEmpty(password) || password.length() < MIN_PASSWORD_LENGTH || password.matches(BAD_PASSWORD_PATTERN));
    }

    static long tryParseLong(String s, long defaultValue) {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }


    public static int getLongestRepeats(@NonNull String text, char repeatedChar) {
        return getLongestRepeats(text.toCharArray(), repeatedChar);
    }

    public static int getLongestRepeats(@NonNull char[] chars, char repeatedChar) {
        if (chars.length == 0) {
            throw new IllegalArgumentException("chars must be set");
        }
        int[] repeats = new int[chars.length];
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] != repeatedChar) {
                continue;
            }
            if (i == 0) {
                repeats[i] = 1;
            } else {
                repeats[i] = repeats[i - 1] + 1;
            }
        }
        int maxRepeat = 0;
        for (int i = 0; i < repeats.length; i++) {
            if (repeats[i] > maxRepeat) {
                maxRepeat = repeats[i];
            }
        }
        return maxRepeat;
    }

    public static boolean isValidPort(int port) {
        return port > 0 && port <= 65535;
    }

    public static int tryParseInt(String s, int defaultValue) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static String getFirstNonNull(String... strings) {
        if (strings == null) {
            return null;
        }
        for (String string : strings) {
            if (string != null) {
                return string;
            }
        }
        return null;
    }

    public static String getEnv(Env env) {
        return getEnv(env.name());
    }

    public static String getEnv(String name) {
        if (name == null) {
            return null;
        }
        return System.getenv(name);
    }

    public static String getLogFolder() {
        String projectFolder = getEnv(Env.NODE_PROJECT_FOLDER);
        if (isEmpty(projectFolder)) {
            throw new IllegalArgumentException(Env.NODE_PROJECT_FOLDER + " env var is not set");
        }
        return projectFolder + "/logs";
    }

    public static String getTasksFolder() {
        String projectFolder = getEnv(Env.NODE_PROJECT_FOLDER);
        if (isEmpty(projectFolder)) {
            throw new IllegalArgumentException(Env.NODE_PROJECT_FOLDER + " env var is not set");
        }
        return projectFolder + "/tasks";
    }

    static String toURL(String text) {
        if (text != null)
            return text.toLowerCase(Locale.getDefault()).replaceAll("[^A-Za-z0-9]", "-");
        return "";
    }

}
