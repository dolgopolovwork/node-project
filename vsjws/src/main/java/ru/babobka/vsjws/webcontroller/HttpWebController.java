package ru.babobka.vsjws.webcontroller;

import ru.babobka.vsjws.model.FilterResponse;
import ru.babobka.vsjws.model.http.HttpRequest;
import ru.babobka.vsjws.model.http.HttpResponse;
import ru.babobka.vsjws.model.http.ResponseFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import static ru.babobka.vsjws.enumerations.ResponseCode.NOT_IMPLEMENTED;

/**
 * Created by dolgopolov.a on 30.12.15.
 */
public class HttpWebController implements WebController<HttpRequest, HttpResponse> {

    private final List<WebFilter> webFilters = new LinkedList<>();

    public final HttpWebController addWebFilter(WebFilter webFilter) {
        webFilters.add(webFilter);
        return this;
    }

    public final HttpResponse control(HttpRequest request) {
        HttpResponse response = null;
        for (WebFilter filter : webFilters) {
            FilterResponse filterResponse = filter.onFilter(request);
            if (!filterResponse.isProceed()) {
                response = filterResponse.getResponse();
                break;
            }
        }
        if (response == null) {
            switch (request.getMethod()) {
                case GET:
                    response = onGet(request);
                    break;
                case POST:
                    response = onPost(request);
                    break;
                case DELETE:
                    response = onDelete(request);
                    break;
                case PUT:
                    response = onPut(request);
                    break;
                case PATCH:
                    response = onPatch(request);
                    break;
                case HEAD:
                    response = onHead(request);
                    break;
                default:
                    response = ResponseFactory.code(NOT_IMPLEMENTED);
                    break;
            }
        }
        ListIterator<WebFilter> webFilterIterator = webFilters.listIterator(webFilters.size());
        while (webFilterIterator.hasPrevious()) {
            webFilterIterator.previous().afterFilter(request, response);
        }
        return response;
    }

    public HttpResponse onHead(HttpRequest request) {
        return ResponseFactory.code(NOT_IMPLEMENTED);
    }

    public HttpResponse onGet(HttpRequest request) {
        return ResponseFactory.code(NOT_IMPLEMENTED);
    }

    public HttpResponse onPost(HttpRequest request) {
        return ResponseFactory.code(NOT_IMPLEMENTED);
    }

    public HttpResponse onPut(HttpRequest request) {
        return ResponseFactory.code(NOT_IMPLEMENTED);
    }

    public HttpResponse onPatch(HttpRequest request) {
        return ResponseFactory.code(NOT_IMPLEMENTED);
    }

    public HttpResponse onDelete(HttpRequest request) {
        return ResponseFactory.code(NOT_IMPLEMENTED);
    }

}
