package ru.babobka.nodeserials;

import org.junit.Test;
import ru.babobka.nodeserials.enumerations.RequestStatus;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

/**
 * Created by 123 on 15.06.2017.
 */
public class NodeRequestTest {

    @Test
    public void testStop() {
        NodeRequest nodeRequest = NodeRequest.stop(UUID.randomUUID());
        assertEquals(nodeRequest.getRequestStatus(), RequestStatus.STOP);
    }

    @Test
    public void testRace() {
        NodeRequest nodeRequest = NodeRequest.race(UUID.randomUUID(), "test", null);
        assertEquals(nodeRequest.getRequestStatus(), RequestStatus.RACE);
    }

    @Test
    public void testHeartBeat() {
        NodeRequest nodeRequest = NodeRequest.heartBeat();
        assertEquals(nodeRequest.getRequestStatus(), RequestStatus.HEART_BEAT);
    }
}
