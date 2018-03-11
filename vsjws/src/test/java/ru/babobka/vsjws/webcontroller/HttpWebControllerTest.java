package ru.babobka.vsjws.webcontroller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import ru.babobka.vsjws.enumerations.HttpMethod;
import ru.babobka.vsjws.enumerations.ResponseCode;
import ru.babobka.vsjws.model.FilterResponse;
import ru.babobka.vsjws.model.http.HttpRequest;
import ru.babobka.vsjws.model.http.HttpResponse;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created by 123 on 14.08.2017.
 */
public class HttpWebControllerTest {

    private HttpWebController webController;

    private WebFilter webFilter;

    @Before
    public void setUp() {
        webFilter = mock(WebFilter.class);
        webController = Mockito.spy(new HttpWebController());
        webController.addWebFilter(webFilter);
    }

    @Test
    public void testOnHead() throws Exception {
        assertEquals(webController.onHead(mock(HttpRequest.class)).getResponseCode(), ResponseCode.NOT_IMPLEMENTED);
    }

    @Test
    public void testOnGet() throws Exception {
        assertEquals(webController.onGet(mock(HttpRequest.class)).getResponseCode(), ResponseCode.NOT_IMPLEMENTED);
    }

    @Test
    public void testOnPut() throws Exception {
        assertEquals(webController.onPut(mock(HttpRequest.class)).getResponseCode(), ResponseCode.NOT_IMPLEMENTED);
    }

    @Test
    public void testOnPost() throws Exception {
        assertEquals(webController.onPost(mock(HttpRequest.class)).getResponseCode(), ResponseCode.NOT_IMPLEMENTED);
    }

    @Test
    public void testOnDelete() throws Exception {
        assertEquals(webController.onDelete(mock(HttpRequest.class)).getResponseCode(), ResponseCode.NOT_IMPLEMENTED);
    }

    @Test
    public void testOnPatch() throws Exception {
        assertEquals(webController.onPatch(mock(HttpRequest.class)).getResponseCode(), ResponseCode.NOT_IMPLEMENTED);
    }

    @Test
    public void testOnWebFilterHasResponse() throws Exception {
        HttpRequest request = mock(HttpRequest.class);
        FilterResponse filterResponse = mock(FilterResponse.class);
        when(filterResponse.isProceed()).thenReturn(false);
        HttpResponse response = mock(HttpResponse.class);
        when(filterResponse.getResponse()).thenReturn(response);
        when(webFilter.onFilter(request)).thenReturn(filterResponse);
        assertEquals(webController.control(request), response);
        verify(webFilter).afterFilter(request, response);
    }

    @Test
    public void testOnWebFilterHasNoResponseGet() throws Exception {
        HttpRequest request = mock(HttpRequest.class);
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        FilterResponse filterResponse = mock(FilterResponse.class);
        when(filterResponse.isProceed()).thenReturn(true);
        when(webFilter.onFilter(request)).thenReturn(filterResponse);
        webController.control(request);
        verify(webController).onGet(request);
    }

    @Test
    public void testOnWebFilterHasNoResponsePatch() throws Exception {
        HttpRequest request = mock(HttpRequest.class);
        when(request.getMethod()).thenReturn(HttpMethod.PATCH);
        FilterResponse filterResponse = mock(FilterResponse.class);
        when(filterResponse.isProceed()).thenReturn(true);
        when(webFilter.onFilter(request)).thenReturn(filterResponse);
        webController.control(request);
        verify(webController).onPatch(request);
    }

    @Test
    public void testOnWebFilterHasNoResponsePut() throws Exception {
        HttpRequest request = mock(HttpRequest.class);
        when(request.getMethod()).thenReturn(HttpMethod.PUT);
        FilterResponse filterResponse = mock(FilterResponse.class);
        when(filterResponse.isProceed()).thenReturn(true);
        when(webFilter.onFilter(request)).thenReturn(filterResponse);
        webController.control(request);
        verify(webController).onPut(request);
    }

    @Test
    public void testOnWebFilterHasNoResponseDelete() throws Exception {
        HttpRequest request = mock(HttpRequest.class);
        when(request.getMethod()).thenReturn(HttpMethod.DELETE);
        FilterResponse filterResponse = mock(FilterResponse.class);
        when(filterResponse.isProceed()).thenReturn(true);
        when(webFilter.onFilter(request)).thenReturn(filterResponse);
        webController.control(request);
        verify(webController).onDelete(request);
    }

    @Test
    public void testOnWebFilterHasNoResponsePost() throws Exception {
        HttpRequest request = mock(HttpRequest.class);
        when(request.getMethod()).thenReturn(HttpMethod.POST);
        FilterResponse filterResponse = mock(FilterResponse.class);
        when(filterResponse.isProceed()).thenReturn(true);
        when(webFilter.onFilter(request)).thenReturn(filterResponse);
        webController.control(request);
        verify(webController).onPost(request);
    }

    @Test
    public void testOnWebFilterHasNoResponseHead() throws Exception {
        HttpRequest request = mock(HttpRequest.class);
        when(request.getMethod()).thenReturn(HttpMethod.HEAD);
        FilterResponse filterResponse = mock(FilterResponse.class);
        when(filterResponse.isProceed()).thenReturn(true);
        when(webFilter.onFilter(request)).thenReturn(filterResponse);
        webController.control(request);
        verify(webController).onHead(request);
    }

}


