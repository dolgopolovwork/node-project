package ru.babobka.nodeutils.util;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import lombok.NonNull;

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

    public static String toJsonString(@NonNull Object object) {
        return GSON.toJson(object);
    }

    public static <T> T parseJson(@NonNull String json, @NonNull Class<T> clazz) {
        if (json.isEmpty()) {
            try {
                return clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new IllegalArgumentException("cannot parse json", e);
            }
        }
        return GSON.fromJson(json, clazz);
    }
}
