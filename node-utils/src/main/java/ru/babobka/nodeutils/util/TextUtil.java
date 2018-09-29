package ru.babobka.nodeutils.util;

import ru.babobka.nodeutils.enums.Env;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class TextUtil {

    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final String BAD_PASSWORD_PATTERN = "^[0-9a-z]+$";
    public static final Charset CHARSET = StandardCharsets.UTF_8;
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

    public static boolean isValidPort(String port) {
        if (isEmpty(port)) {
            return false;
        }
        int defaultInvalidValue = -1;
        int portNumber = tryParseInt(port, defaultInvalidValue);
        if (portNumber == defaultInvalidValue) {
            return false;
        }
        return isValidPort(portNumber);
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

    public static String getEnv(Env env) {
        return getEnv(env.name());
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
        ex.printStackTrace(new PrintWriter(errors));
        return errors.toString();
    }

    public static String getStringFromExceptionOneLine(Exception ex) {
        String message = getStringFromException(ex);
        return message.replaceAll("[\r\n\t]+", "\\n");
    }

    public static String beautifyServerName(String serverName, int port) {
        return "'" + serverName + "':" + port;
    }

}
