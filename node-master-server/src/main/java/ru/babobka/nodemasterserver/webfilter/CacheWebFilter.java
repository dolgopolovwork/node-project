package ru.babobka.nodemasterserver.webfilter;

import ru.babobka.nodeutils.container.Container;

import java.nio.charset.Charset;

import ru.babobka.nodemasterserver.service.CacheService;
import ru.babobka.vsjws.enumerations.HttpMethod;
import ru.babobka.vsjws.model.HttpRequest;
import ru.babobka.vsjws.model.HttpResponse;
import ru.babobka.vsjws.model.FilterResponse;
import ru.babobka.vsjws.model.HttpResponse.ResponseCode;
import ru.babobka.vsjws.webcontroller.WebFilter;

public class CacheWebFilter implements WebFilter {

    private CacheService cacheService = Container.getInstance().get(CacheService.class);

    private final Charset charset = Container.getInstance().get(Charset.class);

    @Override
    public void afterFilter(HttpRequest request, HttpResponse response) {
	if (response.getResponseCode() == ResponseCode.OK) {
	    cacheService.putContent(request, new String(response.getContent(), charset));
	}
    }

    @Override
    public FilterResponse onFilter(HttpRequest request) {

	String noCache = request.getUrlParam("noCache");
	if (noCache != null && noCache.equals("true")) {
	    return FilterResponse.proceed();
	} else if (request.getMethod() == HttpMethod.GET) {
	    String cachedContent = cacheService.getContent(request);

	    if (cachedContent != null) {
		return FilterResponse.response(HttpResponse.jsonResponse(cachedContent));
	    }
	}
	return FilterResponse.proceed();
    }
}
