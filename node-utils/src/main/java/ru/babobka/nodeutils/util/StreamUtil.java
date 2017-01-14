package ru.babobka.nodeutils.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONObject;

import ru.babobka.nodeutils.classloder.JarClassLoader;
import ru.babobka.subtask.model.SubTask;

/**
 * Created by dolgopolov.a on 08.07.15.
 */

public final class StreamUtil {

	private StreamUtil() {

	}

	/*
	 * public static String getLocalResourcePath(Class<?> clazz, String
	 * resourceName) { return
	 * clazz.getClassLoader().getResource(resourceName).getPath(); }
	 */

	public static InputStream getLocalResource(Class<?> clazz, String resourceName) throws FileNotFoundException {

		InputStream is = clazz.getClassLoader().getResourceAsStream(resourceName);
		if (is == null) {
			throw new FileNotFoundException();
		}
		return is;
	}

	public static String readFile(InputStream is) {

		try (Scanner scanner = new Scanner(is, TextUtil.CHARSET.name());) {
			scanner.useDelimiter("\\A");
			String lineBreak = "";
			StringBuilder sb = new StringBuilder();
			while (scanner.hasNextLine()) {
				sb.append(lineBreak);
				sb.append(scanner.nextLine());
				lineBreak = "\n";
			}
			return sb.toString();

		}
	}

	public static String readFile(File file) throws IOException {

		StringBuilder content = new StringBuilder();
		String lineBreak = "";
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(new FileInputStream(file), TextUtil.CHARSET.name()));) {
			String sCurrentLine;
			while ((sCurrentLine = reader.readLine()) != null) {
				content.append(lineBreak);
				content.append(sCurrentLine);
				lineBreak = "\n";
			}
		}
		return content.toString();

	}

	public static String readFile(String filePath) throws IOException {
		return readFile(new File(filePath));
	}

	public static List<String> getJarFileListFromFolder(String folderPath) {
		File folder = new File(folderPath);
		File[] listOfFiles = folder.listFiles();
		LinkedList<String> files = new LinkedList<>();
		if (listOfFiles != null) {
			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile() && listOfFiles[i].getAbsolutePath().endsWith(".jar")) {
					files.add(listOfFiles[i].getName());
				}
			}
		}
		return files;
	}

	public static String getRunningFolder() throws URISyntaxException {
		String folder = new File(StreamUtil.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath())
				.toString();
		if (folder.endsWith(".jar")) {
			folder = folder.substring(0, folder.lastIndexOf(File.separator));
		}
		return folder;
	}

	public static JSONObject getConfigJson(String jarFilePath) throws IOException {
		return new JSONObject(readTextFileFromJar(jarFilePath, "task.json"));
	}

	public static String readTextFileFromJar(String jarFilePath, String fileName) throws IOException {
		URL url = new URL("jar:file:" + jarFilePath + "!/" + fileName);
		InputStream is = url.openStream();
		return readFile(is);
	}

	public static SubTask getTaskClassFromJar(final String jarFilePath, String className) throws IOException {
		try {
			JarClassLoader jarLoader = AccessController.doPrivileged(new PrivilegedAction<JarClassLoader>() {
				public JarClassLoader run() {
					try {
						return new JarClassLoader(jarFilePath);
					} catch (IOException e) {
						throw new IllegalStateException(e);
					}
				}
			});
			return (SubTask) (jarLoader.loadClass(className, true).newInstance());
		} catch (ClassNotFoundException | IllegalAccessException | InstantiationException | RuntimeException e) {
			throw new IOException("Can not get " + className, e);
		}
	}

	public static void sendObject(Object object, Socket socket) throws IOException {

		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
		oos.writeObject(object);
		oos.flush();

	}

	@SuppressWarnings("unchecked")
	public static <T> T receiveObject(Socket socket) throws IOException {
		try {
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			Object o = ois.readObject();
			return (T) o;
		} catch (ClassNotFoundException e) {
			throw new IOException(e);
		}
	}

	/*
	 * private static Object byteArrayToObject(byte[] byteArray) throws
	 * IOException { try (ByteArrayInputStream bis = new
	 * ByteArrayInputStream(byteArray); ObjectInput in = new
	 * ObjectInputStream(bis);) { return in.readObject(); } catch
	 * (ClassNotFoundException e) { throw new IOException(e); } }
	 * 
	 * private static byte[] objectToByteArray(Object object) throws IOException
	 * {
	 * 
	 * try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
	 * ObjectOutput out = new ObjectOutputStream(bos);) {
	 * out.writeObject(object); return bos.toByteArray(); } }
	 */

}