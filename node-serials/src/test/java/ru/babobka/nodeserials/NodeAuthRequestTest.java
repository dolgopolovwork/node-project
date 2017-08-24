package ru.babobka.nodeserials;

import org.junit.Test;
import ru.babobka.nodeutils.util.HashUtil;

import static org.junit.Assert.assertArrayEquals;

/**
 * Created by 123 on 01.09.2017.
 */
public class NodeAuthRequestTest {

    @Test(expected = IllegalArgumentException.class)
    public void testNoLogin() {
        new NodeAuthRequest(null, "abc");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoPassword() {
        new NodeAuthRequest("abc", null);
    }

    @Test
    public void testHashedPassword() {
        String password = "abc";
        NodeAuthRequest nodeAuthRequest = new NodeAuthRequest("login", password);
        assertArrayEquals(nodeAuthRequest.getHashedPassword(), HashUtil.sha2(password));
    }
}
