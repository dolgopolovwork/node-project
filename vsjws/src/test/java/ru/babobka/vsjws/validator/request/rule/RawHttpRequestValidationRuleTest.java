package ru.babobka.vsjws.validator.request.rule;

import org.junit.BeforeClass;
import org.junit.Test;
import ru.babobka.vsjws.exception.BadProtocolSpecifiedException;
import ru.babobka.vsjws.exception.InvalidContentLengthException;
import ru.babobka.vsjws.model.http.RawHttpRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 123 on 01.04.2018.
 */
public class RawHttpRequestValidationRuleTest {

    private static RawHttpRequest badMethodRequest;
    private static RawHttpRequest badHttpProtocolRequest;
    private static RawHttpRequest noHeadersRequest;
    private static RawHttpRequest badContentLengthRequest;
    private RawHttpRequestValidationRule validationRule = new RawHttpRequestValidationRule();

    @BeforeClass
    public static void init() {
        Map<String, String> normalHeaders = new HashMap<>();
        normalHeaders.put("Host", "test");
        Map<String, String> badContentLengthHeaders = new HashMap<>();
        badContentLengthHeaders.putAll(normalHeaders);
        badContentLengthHeaders.put("Content-Length", "-1");
        badHttpProtocolRequest = new RawHttpRequest("GET / HTTP/2", normalHeaders, null);
        noHeadersRequest = new RawHttpRequest("GET / HTTP/1.1", null, null);
        badContentLengthRequest = new RawHttpRequest("POST / HTTP/1.1",
                badContentLengthHeaders, null);
        badMethodRequest = new RawHttpRequest("GETS / HTTP/1.1", null, null);
    }

    @Test(expected = InvalidContentLengthException.class)
    public void testBadContentLengthRequest() {
        validationRule.validate(badContentLengthRequest);
    }

    @Test(expected = BadProtocolSpecifiedException.class)
    public void testBadProtocolRequest() {
        validationRule.validate(badHttpProtocolRequest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoHeadersRequest() {
        validationRule.validate(noHeadersRequest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadMethodRequest() {
        validationRule.validate(badMethodRequest);
    }
}
