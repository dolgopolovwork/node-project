package ru.babobka.nodemasterserver.webfilter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.babobka.vsjws.enumerations.HttpMethod;
import ru.babobka.vsjws.enumerations.ResponseCode;
import ru.babobka.vsjws.model.FilterResponse;
import ru.babobka.vsjws.webserver.HttpRequest;
import ru.babobka.vsjws.webserver.HttpResponse;
import ru.babobka.vsjws.webcontroller.WebFilter;

public class JSONWebFilter implements WebFilter {

    private final HttpMethod[] methods;

    public JSONWebFilter(HttpMethod... methods) {
        this.methods = methods;
    }

    @Override
    public void afterFilter(HttpRequest request, HttpResponse response) {

    }

    @Override
    public FilterResponse onFilter(HttpRequest request) {
        for (HttpMethod method : methods) {
            if (request.getMethod() == method) {
                if (!request.getBody().isEmpty() && !isJSONValid(request.getBody())) {
                    return FilterResponse
                            .response(HttpResponse.text("Invalid JSON", ResponseCode.BAD_REQUEST));
                }
            }
        }
        return FilterResponse.proceed();
    }

    private boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

}
