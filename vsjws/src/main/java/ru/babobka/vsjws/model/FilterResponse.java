package ru.babobka.vsjws.model;

import ru.babobka.vsjws.model.http.HttpResponse;

public class FilterResponse {

    private final boolean proceed;

    private final HttpResponse response;

    private FilterResponse(boolean proceed, HttpResponse response) {
        this.proceed = proceed;
        this.response = response;
    }

    public static FilterResponse proceed() {
        return new FilterResponse(true, null);
    }

    public static FilterResponse failed(HttpResponse response) {
        return new FilterResponse(false, response);
    }

    public boolean isProceed() {
        return proceed;
    }

    public HttpResponse getResponse() {
        return response;
    }

}
