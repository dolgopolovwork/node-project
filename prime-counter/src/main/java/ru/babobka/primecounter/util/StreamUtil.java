package ru.babobka.primecounter.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public final class StreamUtil {

	private StreamUtil() {

	}

	public static String readFile(InputStream is) {
		Scanner scanner = null;
		try {
			scanner = new Scanner(is).useDelimiter("\\A");
			return scanner.hasNext() ? scanner.next() : "";

		} finally {
			if (scanner != null) {
				scanner.close();
			}
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}