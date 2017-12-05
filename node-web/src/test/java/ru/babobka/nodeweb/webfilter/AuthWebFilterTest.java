package ru.babobka.nodeweb.webfilter;

import org.junit.Test;
import ru.babobka.nodeutils.util.HashUtil;
import ru.babobka.vsjws.model.FilterResponse;
import ru.babobka.vsjws.model.HttpRequest;
import ru.babobka.vsjws.webcontroller.WebFilter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by 123 on 13.08.2017.
 */
public class AuthWebFilterTest {

    private String login = "abc";
    private String password = HashUtil.hexSha2("123");

    private WebFilter authWebFilter = new AuthWebFilter(login, password);

    @Test
    public void testOnFilter() {
        HttpRequest request = mock(HttpRequest.class);
        when(request.getHeader("X-Login")).thenReturn(login);
        when(request.getHeader("X-Password")).thenReturn(password);
        FilterResponse filterResponse = authWebFilter.onFilter(request);
        assertTrue(filterResponse.isProceed());
    }


    @Test
    public void testOnFilterBadPassword() {
        HttpRequest request = mock(HttpRequest.class);
        when(request.getHeader("X-Login")).thenReturn(login);
        when(request.getHeader("X-Password")).thenReturn("456");
        FilterResponse filterResponse = authWebFilter.onFilter(request);
        assertFalse(filterResponse.isProceed());
    }
}
