package ru.babobka.vsjws.webcontroller;

import org.junit.Test;
import ru.babobka.vsjws.enumerations.ResponseCode;
import ru.babobka.vsjws.model.FilterResponse;
import ru.babobka.vsjws.model.http.HttpRequest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by 123 on 12.06.2017.
 */
public class JSONWebFilterTest {

    private JSONWebFilter jsonWebFilter = new JSONWebFilter();

    @Test
    public void testOnFilter() {
        HttpRequest httpRequest = mock(HttpRequest.class);
        when(httpRequest.getBody()).thenReturn("{}");
        FilterResponse filterResponse = jsonWebFilter.onFilter(httpRequest);
        assertTrue(filterResponse.isProceed());
    }

    @Test
    public void testOnFilterRichJson() {
        HttpRequest httpRequest = mock(HttpRequest.class);
        when(httpRequest.getBody()).thenReturn("{\"abc\":123}");
        FilterResponse filterResponse = jsonWebFilter.onFilter(httpRequest);
        assertTrue(filterResponse.isProceed());
    }

    @Test
    public void testOnFilterEmptyJson() {
        HttpRequest httpRequest = mock(HttpRequest.class);
        when(httpRequest.getBody()).thenReturn("");
        FilterResponse filterResponse = jsonWebFilter.onFilter(httpRequest);
        assertTrue(filterResponse.isProceed());
    }

    @Test
    public void testOnFilterNullJson() {
        HttpRequest httpRequest = mock(HttpRequest.class);
        FilterResponse filterResponse = jsonWebFilter.onFilter(httpRequest);
        assertTrue(filterResponse.isProceed());
    }

    @Test
    public void testOnFilterBadJson() {
        HttpRequest httpRequest = mock(HttpRequest.class);
        when(httpRequest.getBody()).thenReturn("abc");
        FilterResponse filterResponse = jsonWebFilter.onFilter(httpRequest);
        assertFalse(filterResponse.isProceed());
        assertEquals(filterResponse.getResponse().getResponseCode(), ResponseCode.BAD_REQUEST);
    }

}
