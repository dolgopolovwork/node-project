package ru.babobka.nodeserials.serializer;

import lombok.NonNull;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.exception.NodeSerializationException;

import java.io.*;

public class NodeSerializer {

    private NodeSerializer() {
    }

    public static byte[] serializeRequest(@NonNull NodeRequest request) throws NodeSerializationException {
        try {
            return serialize(request);
        } catch (Exception e) {
            throw new NodeSerializationException("cannot serialize node response", e);
        }
    }

    public static byte[] serializeResponse(@NonNull NodeResponse response) throws NodeSerializationException {
        try {
            return serialize(response);
        } catch (Exception e) {
            throw new NodeSerializationException("cannot serialize node response", e);
        }
    }

    private static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        return out.toByteArray();
    }

    public static NodeResponse deserializeResponse(@NonNull byte[] data) throws NodeSerializationException {
        try {
            return (NodeResponse) deserialize(data);
        } catch (Exception e) {
            throw new NodeSerializationException("cannot deserialize node response", e);
        }
    }

    public static NodeRequest deserializeRequest(@NonNull byte[] data) throws NodeSerializationException {
        try {
            return (NodeRequest) deserialize(data);
        } catch (Exception e) {
            throw new NodeSerializationException("cannot deserialize node request", e);
        }
    }

    private static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return is.readObject();
    }
}
