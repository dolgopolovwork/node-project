package ru.babobka.nodemasterserver.webfilter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;

import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import ru.babobka.nodemasterserver.server.MasterServer;
import ru.babobka.nodeutils.container.ContainerStrategyException;
import ru.babobka.vsjws.model.FilterResponse;
import ru.babobka.vsjws.model.HttpRequest;
import ru.babobka.vsjws.model.HttpResponse;

public class CacheWebFilterITCase {

	private static final String DUMMY_REQUEST_BODY = "Hello World";

	private static final String VALID_JSON_RESPONSE = "{ \"name\":\"John\", \"age\":31, \"city\":\"New York\" }";

	private static CacheWebFilter cacheFilter;

	@BeforeClass
	public static void setUp() throws ContainerStrategyException, FileNotFoundException {
		MasterServer.initTestContainer();

		cacheFilter = new CacheWebFilter();
	}

	private HttpRequest createRequest(HttpRequest.HttpMethod method, String body, String uri) {
		HttpRequest request = new HttpRequest();
		request.setBody(body);
		if (uri != null) {
			request.setUri(uri);
		}
		request.setMethod(method);
		return request;
	}

	private HttpResponse createResponse(HttpResponse.ResponseCode code, String body) {
		return HttpResponse.textResponse(body, code);
	}

	@Test
	public void testGetRequestCache() {
		HttpRequest request = createRequest(HttpRequest.HttpMethod.GET, DUMMY_REQUEST_BODY, null);
		HttpResponse response = createResponse(HttpResponse.ResponseCode.OK, VALID_JSON_RESPONSE);
		FilterResponse filterResponse = cacheFilter.onFilter(request);
		assertTrue(filterResponse.isProceed());
		cacheFilter.afterFilter(request, response);
		filterResponse = cacheFilter.onFilter(request);
		assertFalse(filterResponse.isProceed());
		JSONObject filterJson = new JSONObject(new String(filterResponse.getResponse().getContent()));
		JSONObject expectedJson = new JSONObject(new String(response.getContent()));
		assertEquals(filterJson.toString(), expectedJson.toString());
	}

	@Test
	public void testPutRequestCache() {
		HttpRequest request = createRequest(HttpRequest.HttpMethod.PUT, DUMMY_REQUEST_BODY, null);
		HttpResponse response = createResponse(HttpResponse.ResponseCode.OK, VALID_JSON_RESPONSE);
		FilterResponse filterResponse = cacheFilter.onFilter(request);
		assertTrue(filterResponse.isProceed());
		cacheFilter.afterFilter(request, response);
		filterResponse = cacheFilter.onFilter(request);
		assertTrue(filterResponse.isProceed());
	}

}
