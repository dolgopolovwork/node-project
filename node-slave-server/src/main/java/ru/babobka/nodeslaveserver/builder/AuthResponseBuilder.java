package ru.babobka.nodeslaveserver.builder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeserials.Mappings;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.crypto.RSA;
import ru.babobka.nodeslaveserver.task.TaskPool;

/**
 * Created by dolgopolov.a on 29.10.15.
 */
public class AuthResponseBuilder {

    private static final TaskPool taskPool = Container.getInstance().get(TaskPool.class);

    private static final UUID DUMMY_UUID = new UUID(0, 0);

    private AuthResponseBuilder() {

    }

    public static NodeResponse build(RSA rsa, String user, String password) {

        Map<String, Serializable> dataMap = new HashMap<>();
        dataMap.put("login", user);
        dataMap.put("password", rsa.encrypt(password));
        List<String> tasksList = new LinkedList<>();
        tasksList.addAll(taskPool.getTasksMap().keySet());
        dataMap.put("tasksList", (Serializable) tasksList);
        return new NodeResponse(DUMMY_UUID, DUMMY_UUID, 0, NodeResponse.Status.NORMAL, null, dataMap,
                Mappings.AUTH_TASK_NAME);

    }

}
