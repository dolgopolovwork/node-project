package ru.babobka.vsjws.webcontroller;

import ru.babobka.vsjws.model.FilterResponse;
import ru.babobka.vsjws.model.http.HttpRequest;
import ru.babobka.vsjws.model.http.HttpResponse;

public interface WebFilter {

    FilterResponse onFilter(HttpRequest request);

    void afterFilter(HttpRequest request, HttpResponse response);

}
