package ru.babobka.nodesecurity.service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.util.ArrayUtil;
import ru.babobka.nodeutils.util.HashUtil;
import ru.babobka.nodeutils.util.TextUtil;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by 123 on 05.09.2018.
 */
public class SecureJSONService {
    private final AESService aesService = Container.getInstance().get(AESService.class);
    private final Gson gson = new Gson();

    public byte[] encrypt(Serializable object, String password) throws IOException {
        if (object == null) {
            throw new IllegalArgumentException("cannot encrypt null object");
        } else if (TextUtil.isEmpty(password)) {
            throw new IllegalArgumentException("cannot encrypt using empty key");
        }
        byte[] key = HashUtil.md5(password.getBytes(TextUtil.CHARSET));
        String jsonObject = gson.toJson(object);
        byte[] jsonBytes = jsonObject.getBytes(TextUtil.CHARSET);
        return aesService.encrypt(jsonBytes, key);
    }

    public <T> T decrypt(byte[] cipherJson, String password, Class<T> clazz) throws IOException {
        if (ArrayUtil.isEmpty(cipherJson)) {
            throw new IllegalArgumentException("cannot decrypt empty cipher json");
        } else if (TextUtil.isEmpty(password)) {
            throw new IllegalArgumentException("cannot decrypt using empty password");
        } else if (clazz == null) {
            throw new IllegalArgumentException("class was not specified");
        }
        byte[] key = HashUtil.md5(password.getBytes(TextUtil.CHARSET));
        String jsonObject = new String(aesService.decrypt(cipherJson, key), TextUtil.CHARSET);
        try {
            return gson.fromJson(jsonObject, clazz);
        } catch (JsonSyntaxException e) {
            throw new IOException("cannot decrypt json object. may occur by providing wrong password or storing invalid json in configuration file.", e);
        }
    }
}