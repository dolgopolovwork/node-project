package ru.babobka.nodeserials;

import org.junit.Test;
import ru.babobka.nodeserials.data.Data;

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
        Data data = new Data();
        data.put("abc", "xyz");
        data.put("qwe", "rty");
        NodeData nodeData = new NodeData(null, null, null, 0, data);
        assertEquals(data, nodeData.getData());
    }

    @Test
    public void testGetDataValue() {
        Data data = new Data();
        data.put("abc", "xyz");
        data.put("qwe", "rty");
        NodeData nodeData = new NodeData(null, null, null, 0, data);
        assertEquals((String) nodeData.getDataValue("abc"), (String) data.get("abc"));
    }

    @Test
    public void testGetDataValueDefault() {
        String defaultValue = "default";
        NodeData nodeData = new NodeData(null, null, null, 0, null);
        assertEquals(nodeData.getDataValue("abc", defaultValue), defaultValue);
    }

}
