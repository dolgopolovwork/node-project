package ru.babobka.nodeserials;

import org.junit.Test;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by 123 on 15.06.2017.
 */
public class NodeResponseTest {

    @Test
    public void testFailed() {
        NodeResponse nodeResponse = NodeResponse.failed(UUID.randomUUID());
        assertEquals(nodeResponse.getStatus(), NodeResponse.Status.FAILED);
    }

    @Test
    public void testStopped() {
        NodeResponse nodeResponse = NodeResponse.stopped(UUID.randomUUID());
        assertTrue(nodeResponse.isStopped());
    }

    @Test
    public void testDummy() {
        NodeResponse nodeResponse = NodeResponse.dummy(UUID.randomUUID());
        assertEquals(nodeResponse.getStatus(), NodeResponse.Status.NORMAL);
    }


    @Test
    public void testHeartBeat() {
        NodeResponse nodeResponse = NodeResponse.heartBeat();
        assertTrue(nodeResponse.isHeartBeatingResponse());
    }

    @Test
    public void testGetValue() {
        Map<String, Serializable> map = new HashMap<>();
        map.put("abc", 123);
        NodeResponse nodeResponse = NodeResponse.normal(map, NodeRequest.heartBeatRequest(), 0);
        assertEquals((int) nodeResponse.getDataValue("abc"), 123);
    }

    @Test
    public void testGetValueDefault() {
        Map<String, Serializable> map = new HashMap<>();
        map.put("abc", 123);
        NodeResponse nodeResponse = NodeResponse.normal(map, NodeRequest.heartBeatRequest(), 0);
        assertEquals((int) nodeResponse.getDataValue("abc", -1), 123);
        assertEquals((int) nodeResponse.getDataValue("xyz", -1), -1);
    }
}
