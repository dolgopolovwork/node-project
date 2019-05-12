package ru.babobka.nodeutils.util;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import lombok.NonNull;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by 123 on 05.03.2018.
 */
public class JSONUtil {

    private static final Gson GSON = new Gson();

    private JSONUtil() {

    }

    public static boolean isJSONValid(String json) {
        if (TextUtil.isEmpty(json) || !validBraces(json)) {
            return false;
        }
        try {
            JsonParser parser = new JsonParser();
            parser.parse(json);
        } catch (JsonParseException ex) {
            return false;
        }
        return true;
    }

    private static boolean validBraces(String json) {
        return (json.startsWith("{") && json.endsWith("}")) || (json.startsWith("[") && json.endsWith("]"));
    }

    public static JSONObject toJsonDefault(String body) {
        if (TextUtil.isEmpty(body)) {
            return new JSONObject("{}");
        }
        return new JSONObject(body);

    }

    public static <T> T parseJson(JSONObject jsonObject, Class<T> clazz) {
        return GSON.fromJson(jsonObject.toString(), clazz);
    }

    public static <T extends Serializable> T readJsonFile(
            @NonNull StreamUtil streamUtil,
            @NonNull String pathToJson,
            @NonNull Class<T> clazz) throws IOException {
        if (TextUtil.isEmpty(pathToJson)) {
            throw new IllegalArgumentException("pathToJson is null");
        }
        String fileContent = streamUtil.readFile(pathToJson);
        try {
            return GSON.fromJson(fileContent, clazz);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}
