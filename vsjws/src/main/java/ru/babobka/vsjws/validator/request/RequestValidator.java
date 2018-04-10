package ru.babobka.vsjws.validator.request;

import ru.babobka.nodeutils.validation.Validator;
import ru.babobka.vsjws.model.http.RawHttpRequest;
import ru.babobka.vsjws.validator.request.rule.RawHttpRequestValidationRule;

/**
 * Created by 123 on 01.04.2018.
 */
public class RequestValidator extends Validator<RawHttpRequest> {

    public RequestValidator() {
        super(new RawHttpRequestValidationRule());
    }
}
