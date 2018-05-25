package ru.babobka.nodeserials;

import org.junit.Test;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

}
