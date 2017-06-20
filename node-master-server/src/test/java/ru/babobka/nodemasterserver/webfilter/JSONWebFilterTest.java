package ru.babobka.nodemasterserver.webfilter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import ru.babobka.vsjws.enumerations.HttpMethod;
import ru.babobka.vsjws.enumerations.ResponseCode;
import ru.babobka.vsjws.model.FilterResponse;
import ru.babobka.vsjws.webserver.HttpRequest;

public class JSONWebFilterTest {
    private static final String VALID_JSON = "{ \"name\":\"John\", \"age\":31, \"city\":\"New York\" }";

    private static final String INVALID_JSON = "invalid json";

    private final JSONWebFilter jsonWebFilter = new JSONWebFilter(HttpMethod.PATCH,
            HttpMethod.PUT);

    private HttpRequest createRequest(HttpMethod method, String body) {
        HttpRequest request = mock(HttpRequest.class);
        when(request.getMethod()).thenReturn(method);
        when(request.getBody()).thenReturn(body);
        return request;
    }

    @Test
    public void testInvalidJson() {
        HttpRequest request = createRequest(HttpMethod.PUT, INVALID_JSON);
        FilterResponse response = jsonWebFilter.onFilter(request);
        assertFalse(response.isProceed());
        assertEquals(response.getResponse().getResponseCode(), ResponseCode.BAD_REQUEST);
    }

    @Test
    public void testValidJson() {
        HttpRequest request = createRequest(HttpMethod.PUT, VALID_JSON);
        FilterResponse response = jsonWebFilter.onFilter(request);
        assertTrue(response.isProceed());
    }

    @Test
    public void testIgnoreJson() {
        HttpRequest request = createRequest(HttpMethod.POST, INVALID_JSON);
        FilterResponse response = jsonWebFilter.onFilter(request);
        assertTrue(response.isProceed());
    }

}
