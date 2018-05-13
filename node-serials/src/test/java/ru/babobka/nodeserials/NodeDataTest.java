package ru.babobka.nodeserials;

import org.junit.Test;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Created by 123 on 01.09.2017.
 */
public class NodeDataTest {

    @Test
    public void testNullData() {
        NodeData nodeData = new NodeData(null, null, null, 0, null);
        assertNotNull(nodeData.getData());
    }

    @Test
    public void testData() {
        Map<String, Serializable> map = new HashMap<>();
        map.put("abc", "xyz");
        map.put("qwe", "rty");
        NodeData nodeData = new NodeData(null, null, null, 0, map);
        assertEquals(map, nodeData.getData());
    }

    @Test
    public void testGetDataValue() {
        Map<String, Serializable> map = new HashMap<>();
        map.put("abc", "xyz");
        map.put("qwe", "rty");
        NodeData nodeData = new NodeData(null, null, null, 0, map);
        assertEquals(nodeData.getDataValue("abc"), map.get("abc"));
    }

    @Test
    public void testGetDataValueDefault() {
        String defaultValue = "default";
        NodeData nodeData = new NodeData(null, null, null, 0, null);
        assertEquals(nodeData.getDataValue("abc", defaultValue), defaultValue);
    }

    @Test
    public void testGetStringDataValue() {
        Map<String, Serializable> map = new HashMap<>();
        map.put("abc", 123);
        map.put("qwe", 456);
        NodeData nodeData = new NodeData(null, null, null, 0, map);
        assertEquals(nodeData.getStringDataValue("abc"), String.valueOf(map.get("abc")));
    }

    @Test
    public void testGetStringDataValueEmpty() {
        NodeData nodeData = new NodeData(null, null, null, 0, null);
        assertEquals(nodeData.getStringDataValue("abc"), "");
    }

    @Test
    public void testGetHashSameObject() {
        UUID id = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        String taskName = "testTask";
        long timeStamp = 0;
        Map<String, Serializable> map = new HashMap<>();
        NodeData nodeData = new NodeData(id, taskId, taskName, timeStamp, map);
        assertArrayEquals(nodeData.getHash(), nodeData.getHash());
    }

    @Test
    public void testGetHashSameObjectWithData() {
        UUID id = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        String taskName = "testTask";
        long timeStamp = 0;
        Map<String, Serializable> map = new HashMap<>();
        map.put("abc", 123);
        map.put("xyz", "test");
        NodeData nodeData = new NodeData(id, taskId, taskName, timeStamp, map);
        assertArrayEquals(nodeData.getHash(), nodeData.getHash());
    }


    @Test
    public void testGetHashTwoEqualObjects() {
        UUID id = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        String taskName = "testTask";
        long timeStamp = 0;
        Map<String, Serializable> map = new HashMap<>();
        map.put("abc", 123);
        map.put("xyz", "test");
        NodeData nodeData1 = new NodeData(id, taskId, taskName, timeStamp, map);
        NodeData nodeData2 = new NodeData(id, taskId, taskName, timeStamp, map);
        assertArrayEquals(nodeData1.getHash(), nodeData2.getHash());
    }

    @Test
    public void testGetHashDifferentTime() {
        UUID id = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        String taskName = "testTask";
        long timeStamp = 0;
        Map<String, Serializable> map = new HashMap<>();
        map.put("abc", 123);
        map.put("xyz", "test");
        NodeData nodeData1 = new NodeData(id, taskId, taskName, timeStamp, map);
        NodeData nodeData2 = new NodeData(id, taskId, taskName, timeStamp + 1, map);
        assertFalse(Arrays.equals(nodeData1.getHash(), nodeData2.getHash()));
    }

    @Test
    public void testGetHashDifferentData() {
        UUID id = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        String taskName = "testTask";
        long timeStamp = 0;
        Map<String, Serializable> map1 = new HashMap<>();
        map1.put("abc", 123);
        map1.put("xyz", "test");
        Map<String, Serializable> map2 = new HashMap<>();
        map1.put("abc", 456);
        map1.put("xyz", "test");
        NodeData nodeData1 = new NodeData(id, taskId, taskName, timeStamp, map1);
        NodeData nodeData2 = new NodeData(id, taskId, taskName, timeStamp, map2);
        assertFalse(Arrays.equals(nodeData1.getHash(), nodeData2.getHash()));
    }

    @Test
    public void testGetHashSameData() {
        UUID id = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        String taskName = "testTask";
        long timeStamp = 0;
        Map<String, Serializable> map1 = new HashMap<>();
        map1.put("abc", 123);
        map1.put("xyz", "test");
        Map<String, Serializable> map2 = new HashMap<>();
        map2.put("abc", 123);
        map2.put("xyz", "test");
        NodeData nodeData1 = new NodeData(id, taskId, taskName, timeStamp, map1);
        NodeData nodeData2 = new NodeData(id, taskId, taskName, timeStamp, map2);
        assertArrayEquals(nodeData1.getHash(), nodeData2.getHash());
    }

    @Test
    public void testGetHashDifferentTaskName() {
        UUID id = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        String taskName = "testTask";
        long timeStamp = 0;
        Map<String, Serializable> map = new HashMap<>();
        NodeData nodeData1 = new NodeData(id, taskId, taskName, timeStamp, map);
        NodeData nodeData2 = new NodeData(id, taskId, taskName + "test", timeStamp, map);
        assertFalse(Arrays.equals(nodeData1.getHash(), nodeData2.getHash()));
    }

    @Test
    public void testGetHashDifferentId() {
        UUID taskId = UUID.randomUUID();
        String taskName = "testTask";
        long timeStamp = 0;
        Map<String, Serializable> map = new HashMap<>();
        NodeData nodeData1 = new NodeData(UUID.randomUUID(), taskId, taskName, timeStamp, map);
        NodeData nodeData2 = new NodeData(UUID.randomUUID(), taskId, taskName, timeStamp, map);
        assertFalse(Arrays.equals(nodeData1.getHash(), nodeData2.getHash()));
    }
}
