package ru.babobka.nodeclient.console;

import lombok.NonNull;
import org.apache.commons.cli.Option;
import ru.babobka.nodeutils.util.TextUtil;
import ru.babobka.nodeutils.validation.ValidationRule;

import java.util.Locale;
import java.util.Scanner;

public abstract class CLIUtils {

    private static final String PASSWORD_WARNING = "WARNING! Be careful with password, " +
            "because it will be unable to restore it later";

    public static void printErr(String msg) {
        System.err.println(msg);
        littlePause();
    }

    public static void print(String msg) {
        System.out.println(msg);
        littlePause();
    }

    protected static void printLabel(String msg) {
        print("=== " + msg.toUpperCase(Locale.getDefault()) + " ===");
    }

    //Made to sync err and out streams
    private static void littlePause() {
        try {
            Thread.sleep(50);
        } catch (InterruptedException ignored) {
        }
    }

    protected static Option createArgOption(@NonNull String optionName, @NonNull String description) {
        return Option.builder()
                .longOpt(optionName)
                .hasArg()
                .desc(description)
                .build();
    }

    protected static Option createRequiredArgOption(@NonNull String optionName, @NonNull String description) {
        return Option.builder()
                .longOpt(optionName)
                .hasArg()
                .desc(description)
                .required()
                .build();
    }

    protected static Option createFlagOption(@NonNull String optionName, @NonNull String description) {
        return Option.builder().longOpt(optionName).
                desc(description).build();
    }

    protected static String readLine(String message) {
        return readLine(message, null);
    }

    protected static String readLine(String message, ValidationRule<String> rule) {
        Scanner scanner = new Scanner(System.in, TextUtil.CHARSET.name());
        String line;
        while (true) {
            print(message);
            line = scanner.next();
            if (rule != null) {
                try {
                    rule.validate(line);
                    break;
                } catch (RuntimeException e) {
                    printErr(e.getMessage());
                }
            } else {
                break;
            }
        }
        return line;
    }

    protected static boolean readYesNo(String message) {
        Scanner scanner = new Scanner(System.in, TextUtil.CHARSET.name());
        String line;
        while (true) {
            print(message + "[y/n]");
            line = scanner.next().toLowerCase(Locale.getDefault());
            if (!(line.equals("y") || line.equals("n"))) {
                printErr("You must type either 'y' or 'n'");
            } else {
                break;
            }
        }
        return line.equals("y");
    }

    protected static String readPassword(String message) {
        print(PASSWORD_WARNING);
        Scanner scanner = new Scanner(System.in, TextUtil.CHARSET.name());
        String password;
        String repeatedPassword;
        while (true) {
            print(message);
            password = scanner.next();
            if (TextUtil.isBadPassword(password)) {
                printErr("Weak password. " +
                        "Try bigger one, use both lower and uppercase letters, add some symbols and etc.");
            } else {
                printErr("Please repeat password");
                repeatedPassword = scanner.next();
                if (!password.equals(repeatedPassword)) {
                    printErr("Passwords are not equal");
                } else {
                    break;
                }
            }
        }
        return password;
    }
}
