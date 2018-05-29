package ru.babobka.vsjws.validator.request.rule;

import ru.babobka.nodeutils.util.TextUtil;
import ru.babobka.nodeutils.validation.ValidationRule;
import ru.babobka.vsjws.enumerations.HttpMethod;
import ru.babobka.vsjws.exception.BadProtocolSpecifiedException;
import ru.babobka.vsjws.exception.InvalidContentLengthException;
import ru.babobka.vsjws.model.http.HttpFirstLine;
import ru.babobka.vsjws.model.http.HttpRequest;
import ru.babobka.vsjws.model.http.RawHttpRequest;

import static ru.babobka.vsjws.enumerations.HttpMethod.isMethodWithContent;


/**
 * Created by 123 on 01.04.2018.
 */
public class RawHttpRequestValidationRule implements ValidationRule<RawHttpRequest> {
    @Override
    public void validate(RawHttpRequest rawHttpRequest) {
        if (rawHttpRequest.getFirstLine() == null) {
            throw new IllegalArgumentException("first line is empty");
        } else if (!rawHttpRequest.getHeaders().containsKey("Host")) {
            throw new IllegalArgumentException("header 'Host' was not set");
        }
        HttpFirstLine firstLine = rawHttpRequest.getFirstLine();
        String method = firstLine.getMethod();
        int contentLength = TextUtil.tryParseInt(rawHttpRequest.getHeaders().get(HttpRequest.CONTENT_LENGTH_HEADER), -1);
        if (method == null) {
            throw new IllegalArgumentException("HTTP method was not specified");
        } else if (!HttpMethod.isValidMethod(method)) {
            throw new IllegalArgumentException("HTTP method is invalid");
        } else if (isMethodWithContent(method) && contentLength == -1) {
            throw new InvalidContentLengthException();
        } else if (!HttpRequest.PROTOCOL.equals(firstLine.getProtocol())) {
            throw new BadProtocolSpecifiedException("Bad protocol " + firstLine.getProtocol());
        }
    }
}
