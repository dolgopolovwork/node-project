package ru.babobka.nodeclient.console;

import org.apache.commons.cli.*;
import ru.babobka.nodeutils.util.TextUtil;
import ru.babobka.nodeutils.validation.ValidationRule;

import java.util.Locale;
import java.util.Scanner;

/**
 * Created by 123 on 28.01.2018.
 */
public abstract class CLI {

    private static final String PASSWORD_WARNING = "WARNING! Be careful with password, because it will be unable to restore it later";

    public static void printErr(String msg) {
        System.err.println(msg);
        littlePause();
    }

    public static void print(String msg) {
        System.out.println(msg);
        littlePause();
    }

    public static void printLabel(String msg) {
        print("=== " + msg.toUpperCase(Locale.getDefault()) + " ===");
    }

    //Made to sync err and out streams
    private static void littlePause() {
        try {
            Thread.sleep(50);
        } catch (InterruptedException ignored) {
        }
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
                printErr("Weak password. Try bigger one, use both lower and uppercase letters, add some symbols and etc.");
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

    protected abstract Options createOptions();

    protected abstract void run(CommandLine cmd) throws Exception;

    public abstract String getAppName();

    protected void extraValidation(CommandLine cmd) throws ParseException {
        //do nothing on default
    }

    public void onMain(String[] args) {
        CommandLineParser parser = createParser();
        Options cmdOptions = createOptions();
        CommandLine cmd;
        try {
            cmd = parser.parse(cmdOptions, args);
            extraValidation(cmd);
        } catch (ParseException | RuntimeException e) {
            printErr(e.getMessage());
            printHelp(cmdOptions);
            return;
        }
        try {
            run(cmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    CommandLineParser createParser() {
        return new DefaultParser();
    }

    void printHelp(Options cmdOptions) {
        new HelpFormatter().printHelp(getAppName(), cmdOptions);
    }
}
