package ru.babobka.vsjws.enumerations;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by 123 on 12.06.2017.
 */
public class HttpMethodTest {

    @Test
    public void testIsValidMethod() {
        for (HttpMethod method : HttpMethod.values()) {
            assertTrue(HttpMethod.isValidMethod(method.toString()));
        }
    }


    @Test
    public void testIsValidMethodWrongValue() {
        assertFalse(HttpMethod.isValidMethod("123"));
    }
}
