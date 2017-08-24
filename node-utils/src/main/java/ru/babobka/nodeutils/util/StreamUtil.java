package ru.babobka.nodeutils.util;

import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;


/**
 * Created by dolgopolov.a on 08.07.15.
 */

public class StreamUtil {

    public InputStream getLocalResource(Class<?> clazz, String resourceName) throws FileNotFoundException {
        InputStream is = clazz.getClassLoader().getResourceAsStream(resourceName);
        if (is == null) {
            throw new FileNotFoundException();
        }
        return is;
    }

    public String readFile(InputStream is) {

        try (Scanner scanner = new Scanner(is, TextUtil.CHARSET.name())) {
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

    public String readFile(File file) throws IOException {

        StringBuilder content = new StringBuilder();
        String lineBreak = "";
        try (FileInputStream fis = new FileInputStream(file);
             BufferedReader reader = new BufferedReader(new InputStreamReader(fis, TextUtil.CHARSET.name()))) {
            String sCurrentLine;
            while ((sCurrentLine = reader.readLine()) != null) {
                content.append(lineBreak);
                content.append(sCurrentLine);
                lineBreak = "\n";
            }
        }
        return content.toString();

    }

    public String readFile(String filePath) throws IOException {
        return readFile(new File(filePath));
    }

    public List<String> getJarFileListFromFolder(String folderPath) {
        File folder = new File(folderPath);
        File[] listOfFiles = folder.listFiles();
        LinkedList<String> files = new LinkedList<>();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile() && file.getAbsolutePath().endsWith(".jar")) {
                    files.add(file.getName());
                }
            }
        }
        return files;
    }

    public String getRunningFolder() throws URISyntaxException {
        String folder = new File(StreamUtil.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath())
                .toString();
        if (folder.endsWith(".jar")) {
            folder = folder.substring(0, folder.lastIndexOf(File.separator));
        }
        return folder;
    }

    public String readTextFileFromJar(String jarFilePath, String fileName) throws IOException {
        URL url = new URL("jar:file:" + jarFilePath + "!/" + fileName);
        InputStream is = url.openStream();
        return readFile(is);
    }

    public void sendObject(Object object, Socket socket) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(object);
        oos.flush();
    }

    @SuppressWarnings("unchecked")
    public <T> T receiveObject(Socket socket) throws IOException {
        try {
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Object o = ois.readObject();
            return (T) o;
        } catch (ClassNotFoundException e) {
            throw new IOException(e);
        }
    }

}