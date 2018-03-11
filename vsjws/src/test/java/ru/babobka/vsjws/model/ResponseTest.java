package ru.babobka.vsjws.model;

import org.junit.Test;
import ru.babobka.vsjws.enumerations.RestrictedHeader;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by 123 on 12.06.2017.
 */
public class ResponseTest {

    @Test
    public void testIsRestrictedHeader() {
        for (RestrictedHeader header : RestrictedHeader.values()) {
            assertTrue(Response.isRestrictedHeader(header.toString()));
        }
    }

    @Test
    public void testIsNotRestrictedHeader() {
        assertFalse(Response.isRestrictedHeader("abc"));

    }
}
