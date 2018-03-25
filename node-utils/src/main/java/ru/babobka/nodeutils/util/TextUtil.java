package ru.babobka.nodeutils.util;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class TextUtil {

    static final Charset CHARSET = StandardCharsets.UTF_8;

    private static final String UUID_PATTERN = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";

    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private static final String DIGITS_PATTERN = "[0-9]+";

    private TextUtil() {

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

    public static boolean isNumber(String text) {
        return !isEmpty(text) && text.matches(DIGITS_PATTERN);
    }

    static boolean isValidUUID(String uuid) {
        return !isEmpty(uuid) && uuid.matches(UUID_PATTERN);
    }

    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }

    static long tryParseLong(String s, long defaultValue) {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static void hideWarnings(String... prefixes) {
        try {
            PrintStream filterOut = new PrintStream(System.err, true, "UTF-8") {
                public void println(String line) {
                    if (!startsWith(line, prefixes))
                        super.println(line);
                }
            };
            System.setErr(filterOut);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private static boolean startsWith(String text, String[] prefixes) {
        if (text == null || prefixes == null) {
            return false;
        }
        for (String prefix : prefixes) {
            if (text.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    public static String arrayToString(Object... objects) {
        if (objects == null || objects.length == 0) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        String indent = "";
        for (Object object : objects) {
            stringBuilder.append(indent).append(object);
            indent = ", ";
        }
        return stringBuilder.toString();
    }

    public static int[] getLongestRepeats(String text, char repeatedChar) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("text must be set");
        }
        int[] repeats = new int[text.length()];
        char[] chars = text.toCharArray();
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
        return repeats;
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

    public static int tryParseInt(String s) {
        return tryParseInt(s, 0);
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

    public static String getEnv(String name) {
        if (name == null) {
            return null;
        }
        return System.getenv(name);
    }

    static String toURL(String text) {
        if (text != null)
            return text.toLowerCase(Locale.getDefault()).replaceAll("[^A-Za-z0-9]", "-");
        return "";
    }

    public static String getStringFromException(Exception ex) {
        StringWriter errors = new StringWriter();
        //TODO закрыть нужно или че?
        ex.printStackTrace(new PrintWriter(errors));
        return errors.toString();
    }

    public static String beautifyServerName(String serverName, int port) {
        return "'" + serverName + "':" + port;
    }

}
