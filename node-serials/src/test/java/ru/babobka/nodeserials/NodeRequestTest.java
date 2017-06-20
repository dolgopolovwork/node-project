package ru.babobka.nodeserials;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertTrue;

/**
 * Created by 123 on 15.06.2017.
 */
public class NodeRequestTest {

    @Test
    public void testStop() {
        NodeRequest nodeRequest = NodeRequest.stop(UUID.randomUUID(), "test");
        assertTrue(nodeRequest.isStoppingRequest());
    }

    @Test
    public void testRace() {
        NodeRequest nodeRequest = NodeRequest.race(UUID.randomUUID(), "test", null);
        assertTrue(nodeRequest.isRaceStyle());
    }

    @Test
    public void testHeartBeat() {
        NodeRequest nodeRequest = NodeRequest.heartBeatRequest();
        assertTrue(nodeRequest.isHeartBeatingRequest());
    }
}
