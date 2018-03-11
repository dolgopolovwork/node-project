package ru.babobka.nodeutils.util;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
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
        if (json == null || json.isEmpty() || !validBraces(json)) {
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
        if (body == null || body.isEmpty()) {
            return new JSONObject("{}");
        }
        return new JSONObject(body);

    }

    public static <T> T parseJson(JSONObject jsonObject, Class<T> clazz) {
        return GSON.fromJson(jsonObject.toString(), clazz);
    }

    public static <T extends Serializable> T readJsonFile(StreamUtil streamUtil, String pathToJson, Class<T> clazz) throws IOException {
        if (streamUtil == null) {
            throw new IllegalArgumentException("streamUtil is null");
        } else if (TextUtil.isEmpty(pathToJson)) {
            throw new IllegalArgumentException("pathToJson is null");
        } else if (clazz == null) {
            throw new IllegalArgumentException("class was not specified");
        }
        String fileContent = streamUtil.readFile(pathToJson);
        try {
            return GSON.fromJson(fileContent, clazz);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}
