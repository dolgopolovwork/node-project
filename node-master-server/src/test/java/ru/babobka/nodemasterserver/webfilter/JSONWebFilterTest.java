package ru.babobka.nodemasterserver.webfilter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ru.babobka.vsjws.model.FilterResponse;
import ru.babobka.vsjws.model.HttpRequest;
import ru.babobka.vsjws.model.HttpResponse;

public class JSONWebFilterTest {

    private static final String VALID_JSON = "{ \"name\":\"John\", \"age\":31, \"city\":\"New York\" }";

    private static final String INVALID_JSON = "invalid json";

    private final JSONWebFilter jsonWebFilter = new JSONWebFilter(HttpRequest.HttpMethod.PATCH,
	    HttpRequest.HttpMethod.PUT);

    private HttpRequest createRequest(HttpRequest.HttpMethod method, String body) {
	HttpRequest request = new HttpRequest();
	request.setMethod(method);
	request.setBody(body);
	return request;
    }

    @Test
    public void testInvalidJson() {
	HttpRequest request = createRequest(HttpRequest.HttpMethod.PUT, INVALID_JSON);
	FilterResponse response = jsonWebFilter.onFilter(request);
	assertFalse(response.isProceed());
	assertEquals(response.getResponse().getResponseCode(), HttpResponse.ResponseCode.BAD_REQUEST);
    }

    @Test
    public void testValidJson() {
	HttpRequest request = createRequest(HttpRequest.HttpMethod.PUT, VALID_JSON);
	FilterResponse response = jsonWebFilter.onFilter(request);
	assertTrue(response.isProceed());
    }

    @Test
    public void testIgnoreJson() {
	HttpRequest request = createRequest(HttpRequest.HttpMethod.POST, INVALID_JSON);
	FilterResponse response = jsonWebFilter.onFilter(request);
	assertTrue(response.isProceed());
    }

}
