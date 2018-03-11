package ru.babobka.vsjws.webcontroller;

import ru.babobka.vsjws.model.http.HttpRequest;
import ru.babobka.vsjws.model.http.HttpResponse;
import ru.babobka.vsjws.model.http.ResponseFactory;

import java.io.IOException;
import java.io.InputStream;

import static ru.babobka.vsjws.enumerations.ResponseCode.NOT_FOUND;

public class StaticResourcesController extends HttpWebController {

    @Override
    public HttpResponse onGet(HttpRequest request) {
        String uri = request.getUri();
        String fileName = uri.substring(1);
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName)) {
            if (is != null) {
                return ResponseFactory.resource(is);
            }
        } catch (IOException e) {
            return ResponseFactory.exception(e);
        }
        return ResponseFactory.code(NOT_FOUND);
    }

}
