package ru.babobka.nodemasterserver.service;

import ru.babobka.vsjws.webserver.HttpRequest;

public interface CacheService {

    void putContent(HttpRequest request, String content);

    String getContent(HttpRequest request);

}
