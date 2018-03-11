package ru.babobka.vsjws.webcontroller.it;

import ru.babobka.vsjws.model.http.HttpRequest;
import ru.babobka.vsjws.model.http.HttpResponse;
import ru.babobka.vsjws.webcontroller.HttpWebController;

/**
 * Created by dolgopolov.a on 30.12.15.
 */
public class InternalErrorController extends HttpWebController {

    @Override
    public HttpResponse onGet(HttpRequest request) {
        throw new NullPointerException();
    }

}
