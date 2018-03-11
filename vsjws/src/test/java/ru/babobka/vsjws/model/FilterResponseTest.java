package ru.babobka.vsjws.model;

import org.junit.Test;
import ru.babobka.vsjws.model.http.HttpResponse;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by 123 on 12.06.2017.
 */
public class FilterResponseTest {

    @Test
    public void testProceed() {
        FilterResponse filterResponse = FilterResponse.proceed();
        assertTrue(filterResponse.isProceed());
    }

    @Test
    public void testResponse() {
        HttpResponse response = mock(HttpResponse.class);
        FilterResponse filterResponse = FilterResponse.failed(response);
        assertEquals(response.getId(), filterResponse.getResponse().getId());
        assertFalse(filterResponse.isProceed());
    }
}
