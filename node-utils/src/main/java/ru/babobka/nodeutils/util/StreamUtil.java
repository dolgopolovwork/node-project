package ru.babobka.nodeutils.util;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by dolgopolov.a on 08.07.15.
 */

public class StreamUtil {

    private static final int DEFAULT_LOOP_BACK = 50;

    public ServerSocket createServerSocket(int port, boolean local) throws IOException {
        if (local) {
            return new ServerSocket(port, DEFAULT_LOOP_BACK, InetAddress.getByName(null));
        }
        return new ServerSocket(port, DEFAULT_LOOP_BACK);
    }

    public InputStream getLocalResource(Class<?> clazz, String resourceName) throws FileNotFoundException {
        InputStream is = clazz.getClassLoader().getResourceAsStream(resourceName);
        if (is == null) {
            throw new FileNotFoundException();
        }
        return is;
    }

    public void writeBytesToFile(byte[] bytes, String filePath) throws IOException {
        if (ArrayUtil.isEmpty(bytes)) {
            throw new IllegalArgumentException("cannot write empty byte array to a file");
        } else if (TextUtil.isEmpty(filePath)) {
            throw new IllegalArgumentException("file path was not set");
        }
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(bytes);
        }
    }

    public void writeTextToFile(String text, String filePath) throws IOException {
        if (text == null) {
            throw new IllegalArgumentException("cannot write null text to file");
        } else if (TextUtil.isEmpty(filePath)) {
            throw new IllegalArgumentException("file path was not set");
        }
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(text.getBytes(TextUtil.CHARSET));
        }
    }

    public byte[] readBytesFromFile(String filePath) throws IOException {
        if (TextUtil.isEmpty(filePath)) {
            throw new IllegalArgumentException("file path was not set");
        }
        File file = new File(filePath);
        return Files.readAllBytes(file.toPath());
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
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

}