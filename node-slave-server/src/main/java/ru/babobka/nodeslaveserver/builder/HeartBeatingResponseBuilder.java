package ru.babobka.nodeslaveserver.builder;

import java.util.UUID;

import ru.babobka.nodeserials.Mappings;
import ru.babobka.nodeserials.NodeResponse;

public class HeartBeatingResponseBuilder {

    private static final UUID DUMMY_UUID = new UUID(0, 0);

    private HeartBeatingResponseBuilder() {

    }

    public static NodeResponse build() {
	return new NodeResponse(DUMMY_UUID, DUMMY_UUID, 0, NodeResponse.Status.NORMAL, null, null,
		Mappings.HEART_BEAT_TASK_NAME);
    }

}
