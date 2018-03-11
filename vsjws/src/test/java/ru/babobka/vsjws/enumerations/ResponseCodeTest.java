package ru.babobka.vsjws.enumerations;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by 123 on 12.06.2017.
 */
public class ResponseCodeTest {

    @Test
    public void testGetCode() {
        assertEquals(ResponseCode.OK.getCode(), 200);
        assertEquals(ResponseCode.NOT_FOUND.getCode(), 404);
        assertEquals(ResponseCode.INTERNAL_SERVER_ERROR.getCode(), 500);
    }
}
