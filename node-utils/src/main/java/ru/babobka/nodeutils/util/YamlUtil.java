package ru.babobka.nodeutils.util;

import lombok.NonNull;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;

public class YamlUtil {

    private static Yaml yaml = new Yaml();

    private YamlUtil() {

    }

    public static <T> T read(@NonNull String localYamlFile, @NonNull Class<T>
            clazz) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(localYamlFile);
            if (inputStream == null) {
                throw new IOException("No file '" + localYamlFile + "' was found");
            }
            return yaml.loadAs(
                    inputStream,
                    clazz);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

}
